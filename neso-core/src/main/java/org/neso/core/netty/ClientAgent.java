package org.neso.core.netty;

import static io.netty.util.internal.StringUtil.NEWLINE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.neso.core.exception.ClientAbortException;
import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAgent implements Client {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	 
	final private String remoteAddr;
	final private long connectionTime;
	final private ServerContext serverContext;
    
    final private int writeTimeoutMillis;
    
	final private SocketChannel sc;
	
	final ByteLengthBasedReader headBodyRequestReader;
	final Bbw writer;
	final private RequestHandler requestHandler;

    final private boolean repeatableResponse;
    private AtomicBoolean writable = new AtomicBoolean(true);
    private AtomicBoolean close = new AtomicBoolean(false);
    
    private Map<String, Object> sessionAttrMap = new LinkedHashMap<String, Object>();
    
    final private ReentrantLock lock = new ReentrantLock();
    
	public ClientAgent(SocketChannel sc, ServerContext serverContext, int writeTimeoutMillis) {
		this.connectionTime = System.currentTimeMillis();
    	if (sc.remoteAddress() instanceof InetSocketAddress) {
        	InetSocketAddress addr = (InetSocketAddress) sc.remoteAddress();
        	this.remoteAddr = addr.getHostName();
    	} else {
    		this.remoteAddr = sc.remoteAddress().toString();
    	}
    	this.sc = sc;
    	this.serverContext = serverContext;
    	
    	if (writeTimeoutMillis < 0) {
    		throw new RuntimeException("writeTimeoutMillis is bigger than zero");
    	}
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	
    	this.requestHandler = serverContext.getRequestHandler(); 
    	
    	this.repeatableResponse = this.requestHandler.getRequestFactory().isRepeatableReceiveRequest();
    	this.headBodyRequestReader = new HeadBodyReader(requestHandler, this);
    	this.writer = new Bbw();
	}
	
	public ByteLengthBasedReader getByteLengthBasedReader() {
		return this.headBodyRequestReader;
	}
	
	@Override
	public String getRemoteAddr() {
    	return this.remoteAddr;
    }
    
	@Override
    public long getConnectionTime() {
    	return this.connectionTime;
    }
    
	@Override
	public ServerContext getServerContext() {
		return this.serverContext;
	}
	
    @Override
    public void addAttribute(String key, Object value) {
    	sessionAttrMap.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public <T> T getAttribute(String key) {
    	if (sessionAttrMap.containsKey(key)) {
        	return (T) sessionAttrMap.get(key);
    	} else {
    		return null;
    	}
    }
 
    @Override
    public boolean removeAttrubute(String key) {
    	if (sessionAttrMap.containsKey(key)) {
        	return sessionAttrMap.remove(key) != null ? true : false;
    	} else {
    		return false;
    	}
    }
    
    private void log(ByteBuf readedBuf) {
    	String eventName = "RESPONSE WRITE";
		int length = readedBuf.readableBytes();
		int offset = readedBuf.readerIndex();
        int rows = length / 16 + (length % 15 == 0? 0 : 1) + 4;
        StringBuilder dump = new StringBuilder(eventName.length() + 2 + 10 + 1 + 2 + rows * 80);

        dump.append(eventName).append(": ").append(length).append('B').append(NEWLINE);
    	ByteBufUtil.appendPrettyHexDump(dump, readedBuf, offset, length);
    	
    	readedBuf.resetReaderIndex();
    	
    	logger.info(dump.toString());
    }
    
    
    @Override
    public boolean isConnected() {
    	return sc.isOpen() && !close.get();
    }
    
    @Override
    public void disconnect() {
    	
    	lock.lock();
    	logger.debug("disconnect lock get");
    	
    	try {
    		
    		if (writer.isTerminated()) {
            	if (sc.isOpen()) {
            		sc.close();
            		logger.debug("disconnected..");
            	} else {
            		logger.debug("already disconnected..");
            	}
    		} else {
    			logger.debug("Disconnect is pending for an unterminated write operation..");
    		}
    		close.set(true);
        	
    	} finally {

    		logger.debug("disconnect lock release!!!!!!!!!!");
    		lock.unlock();
    	}
    }
    
    public ByteBasedWriter getWriter() {
    	logger.debug("writer lock try");
    	lock.lock();
    	logger.debug("writer lock get");
    	return writer;
    }
    
    
    public class Bbw implements ByteBasedWriter {
    	
    	private AtomicInteger writeTaskCount = new AtomicInteger(0);

    	public boolean isTerminated() {
    		return !(writeTaskCount.get() > 0);
    	}
    	
    	@Override
    	public void write(byte b) {
    		write(new byte[]{b});
    	}
    	
		@Override
		public void write(byte[] bytes) {
			ByteBuf buf =  sc.alloc().buffer(bytes.length);
			buf.writeBytes(bytes);
			write(buf);
		}

		@Override
		public void write(ByteBuf buf) {
 
			if (isConnected() && writable.get()) { //open & not close & writable
				
				try {
					
					log(buf);
					
					final ChannelFuture cf = sc.writeAndFlush(buf);
					writeTaskCount.incrementAndGet();
					
					final ScheduledFuture<?> sf = sc.eventLoop().schedule(new Runnable() {
						
						@Override
						public void run() {
							if (!cf.isDone()) {
								try {
									if (writeTaskCount.decrementAndGet() < 1 && close.get()) {
										disconnect();
									}
									
									Bbw.this.writeFailProc(ClientAgent.this, WriteTimeoutException.INSTANCE);
								} catch (Throwable t) {
									Bbw.this.writeFailProc(ClientAgent.this, t);
								}
							} else {
								//쓰기 작업이 실행되었다면 무시..
							}
						}
					}, writeTimeoutMillis, TimeUnit.MILLISECONDS);
					
					cf.addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							sf.cancel(false);
							if (writeTaskCount.decrementAndGet() < 1 && close.get()) {
								disconnect();
							}
							
							if (!future.isSuccess()) {
								Bbw.this.writeFailProc(ClientAgent.this, future.cause());
							} else {
								logger.debug("write...completed");
							}
						}
					});
					
					
                    
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
        	} else {
        		
        		if (writable.get()) {
        			if (!close.get()) {
        				if (!sc.isOpen()) {
        					logger.debug("write fail. Client abort");
        					throw new ClientAbortException(ClientAgent.this);
        				} else {
        					//write success
        				}
        			} else {
        				logger.debug("write fail. disconnecting...");
        			}
        		} else {
        			logger.debug("write fail. already response");
        		}
        	}
		}
 

		private void writeFailProc(Client client, Throwable t) {
			if (client.isConnected() && writable.get()) {
				requestHandler.onExceptionWrite(client, t);
			}
		}

		@Override
		public void close() {
			if (!repeatableResponse) {
				writable.compareAndSet(true, false);
				disconnect();
			}
			logger.debug("writer lock release");
			lock.unlock();
		}
    }
}