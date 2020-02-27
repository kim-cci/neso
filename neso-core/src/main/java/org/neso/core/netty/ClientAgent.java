package org.neso.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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

    final private boolean reWritable;
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
    	
    	this.reWritable = this.requestHandler.getRequestFactory().isRepeatableReceiveRequest();
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
    
    @Override
    public boolean isConnected() {
    	return sc.isOpen() && !close.get();
    }
    
    @Override
    public void disconnect() {
    	
    	logger.debug("disconnect 획득 시도");
    	lock.lock();
    	logger.debug("disconnect 획득");
    	
    	
    	try {
    		close.set(true);
    		
    		if (writer.isTerminated()) {
    			logger.debug("접속 종료 시도..");
    		
            	if (sc.isOpen()) {
            		sc.close();
            		logger.debug("접속 종료");
            	} else {
            		logger.debug("이미 닫혔음");
            	}
    		} else {
    			logger.debug("아직 완료되지 않은 쓰기 작업으로 인해 접속 종료 보류..");
    			return;
    		}

        	
    	} finally {
    		logger.debug("disconnect 반환");
			lock.unlock();
    	}
    }
    
    public ByteBasedWriter getWriter() {
    	logger.debug("writer 획득 시도");
    	lock.lock();
    	logger.debug("writer 획득");
    	return writer;
    }
    
    
    public class Bbw implements ByteBasedWriter {
    	
    	private AtomicInteger writeTaskCount = new AtomicInteger(0);
    	
    	public boolean isTerminated() {
    		return !(writeTaskCount.get() > 0);
    	}
    	
		@Override
		public void write(byte[] bytes) {
			ByteBufAllocator alloc = sc.alloc();
			ByteBuf buf = alloc.buffer(bytes.length);
			buf.writeBytes(bytes);
			write(buf);
		}

		@Override
		public void write(ByteBuf buf) {
 
			if (isConnected() && writable.get()) {
				
				try {
					logger.debug("응답 쓰기 시작");
					
					final ChannelFuture cf = sc.writeAndFlush(buf);
					writeTaskCount.incrementAndGet();
					
					final ScheduledFuture<?> sf = sc.eventLoop().schedule(new Runnable() {
						
						@Override
						public void run() {
							logger.debug("스케쥴된 스레드 실행");
							if (!cf.isDone()) {
								try {
									int cnt = writeTaskCount.decrementAndGet();
									logger.debug("ScheduledFuture... write cnt = {}", cnt);
									if (cnt < 1 && close.get()) {
										disconnect();
									}
									
									Bbw.this.writeFailProc(ClientAgent.this, WriteTimeoutException.INSTANCE);
								} catch (Throwable t) {
									Bbw.this.writeFailProc(ClientAgent.this, t);
								}
							}
						}
					}, writeTimeoutMillis, TimeUnit.MICROSECONDS);
					
					cf.addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							sf.cancel(false);
							int cnt = writeTaskCount.decrementAndGet();
							logger.debug("Write Channel Listener... write cnt = {}", cnt);
							if (cnt < 1 && close.get()) {
								disconnect();
							}
							
							logger.debug("응답에 {} 했습니다", (future.isSuccess() ? "성공" : "실패"));
							
							if (!future.isSuccess()) {
								Bbw.this.writeFailProc(ClientAgent.this, future.cause());
							}
						}
					});
                    
					logger.debug("응답 요청했습니다.");
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
        	} else {
        		logger.debug("쓰기 불가. {}, {}, {}", sc.isOpen(), close, writable);
        		if (writable.get()) {
        			if (!close.get()) {
        				if (!sc.isOpen()) {
        					throw new ClientAbortException(ClientAgent.this);
        				}
        			} else {
        				logger.info("이미 접속 종료 처리");
        			}
        		} else {
        			logger.info("이미 응답처리를 완료하였습니다.");
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
			if (!reWritable) {
				writable.compareAndSet(true, false);
				disconnect();
			}
			logger.debug("writer 반환");
			lock.unlock();
		}
    }
}