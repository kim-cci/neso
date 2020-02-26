package org.neso.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
	final ByteBasedWriter write;
	final private RequestHandler requestHandler;
	 

    /**
     * writeClose일 경우, write는 비동기로 처리되고 write완료 시점에 close처리됨, close되지 않은 상황에 다른 스레드가 write처리를 하면 2번 응답을 받게 된다
     * 따라서. writeClose인 경우, 추가적으로 응답을 내려주지 않게 serverCloseProc 플래그 둠
     * 
     * ==> 변경 됨, write 비동기 처리 -> 동기처리로, writeTimeoutMillis 필수 
     * 이방법이 심플하고 안정적일듯
     */
    private volatile boolean serverCloseProc;
    
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
    	this.serverCloseProc = false;
    	this.requestHandler = serverContext.getRequestHandler(); 
    	
    	this.headBodyRequestReader = new HeadBodyReader(requestHandler, this);
    	this.write = new Bbw();
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
    	return !serverCloseProc && sc.isOpen();
    }
    
    @Override
    public void disconnect() {
    	
    	logger.debug("획득 시도합니다1");
    	lock.lock();
    	logger.debug("획득 했습니다1.");
    	
    	logger.debug("접속을 끊습니다.");
    	try {
    		serverCloseProc = true;
        	if (sc.isOpen()) {
        		sc.close();
        		logger.debug("접속을 끊음");
        	}
        	
    	} finally {
    		logger.debug("반환 시도합니다1.");
			lock.unlock();
			logger.debug("반환했습니다1.");
    	}
    }
    
    public ByteBasedWriter getWriter() {
    	logger.debug("획득 시도합니다");
    	lock.lock();
    	logger.debug("획득 했습니다.");
    	return write;
    }
    
    
    public class Bbw implements ByteBasedWriter {

		@Override
		public void write(byte[] bytes) {
			ByteBufAllocator alloc = sc.alloc();
			ByteBuf buf = alloc.buffer(bytes.length);
			buf.writeBytes(bytes);
			write(buf);
		}

		@Override
		public void write(ByteBuf buf) {
 
			if (isConnected()) {
				
				try {
					logger.debug("응답 보냅니다.");
					
					
					sc.write(buf).addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							logger.debug("응답이 {}했습니다." , (future.isSuccess() ? "성공" : "실패"));
							
                            if (!future.isSuccess()) {
                                requestHandler.onExceptionRequestIO(ClientAgent.this, future.cause());
                            }
                            
                            if (serverCloseProc) {
                            	disconnect();
                            }
						}
					}).get(writeTimeoutMillis, TimeUnit.MILLISECONDS);
                    
					logger.debug("응답 요청했습니다.");
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
        	} else {
        		logger.debug("응답을 보내려고 했지만 이미 닫혔습니다. {}, {}", serverCloseProc, sc.isOpen());
        		if (!serverCloseProc && !sc.isOpen()) {
        			throw new ClientAbortException(ClientAgent.this);
        		}
        	}
			
		}
 

		@Override
		public void flush() {
			sc.flush();
		}

		@Override
		public void close() {
			sc.flush();
			logger.debug("반환 시도합니다.4");
			lock.unlock();
			logger.debug("반환했습니다.4");
		}
		
		@Override
		public void closeAndDisconnect(){
			sc.flush();
			serverCloseProc = true;
			logger.debug("반환 시도합니다.3");
			lock.unlock();
			logger.debug("반환 했습니다.3");
		}
    }
}

//
//@Override
//public void write(ByteBuf buf) {
//
//	if (isConnected()) {
//		
//		try {
//			logger.debug("응답 보냅니다.");
//			sc.write("").get(timeout, unit)
//			sc.write(buf).addListener(new ChannelFutureListener() {
//				
//				@Override
//				public void operationComplete(ChannelFuture future) throws Exception {
//					logger.debug("응답이 {}했습니다." , (future.isSuccess() ? "성공" : "실패"));
//					
//                    if (!future.isSuccess()) {
//                        requestHandler.onExceptionRequestIO(ClientAgent.this, future.cause());
//                    }
//                    
//                    if (serverCloseProc) {
//                    	disconnect();
//                    }
//				}
//			});
//            
//			logger.debug("응답 요청했습니다.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	} else {
//		logger.debug("응답을 보내려고 했지만 이미 닫혔습니다. {}, {}", serverCloseProc, sc.isOpen());
//		if (!serverCloseProc && !sc.isOpen()) {
//			throw new ClientAbortException(ClientAgent.this);
//		}
//	}
//	
//}