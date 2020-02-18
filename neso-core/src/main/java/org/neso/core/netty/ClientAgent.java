package org.neso.core.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.neso.core.exception.ClientAbortException;
import org.neso.core.request.Client;
import org.neso.core.request.factory.InMemoryRequestFactory;
import org.neso.core.request.factory.RequestFactory;
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

	final private RequestHandler requestHandler;
	 

    /**
     * writeClose일 경우, write는 비동기로 처리되고 write완료 시점에 close처리됨, close되지 않은 상황에 다른 스레드가 write처리를 하면 2번 응답을 받게 된다
     * 따라서. writeClose인 경우, 추가적으로 응답을 내려주지 않게 serverClose 플래그 둠
     */
    private volatile boolean serverClose;
    
    private Map<String, Object> sessionAttrMap = new LinkedHashMap<String, Object>();
    
    final private ReentrantLock lock = new ReentrantLock();

    public ClientAgent(SocketChannel sc, ServerContext serverContext, int writeTimeoutMillis) {
    	this(sc, serverContext, writeTimeoutMillis, new InMemoryRequestFactory());
    }
    
	public ClientAgent(SocketChannel sc, ServerContext serverContext, int writeTimeoutMillis, RequestFactory requestFactory) {
		this.connectionTime = System.currentTimeMillis();
    	if (sc.remoteAddress() instanceof InetSocketAddress) {
        	InetSocketAddress addr = (InetSocketAddress) sc.remoteAddress();
        	this.remoteAddr = addr.getHostName();
    	} else {
    		this.remoteAddr = sc.remoteAddress().toString();
    	}
    	this.sc = sc;
    	this.serverContext = serverContext;
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	this.serverClose = false;
    	this.requestHandler = serverContext.getRequestHandler(); 
    	
    	this.headBodyRequestReader = new HeadBodyRequestReader(requestHandler, requestFactory, this);
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
    
    public boolean isConnected() {
    	return !serverClose && sc.isOpen();
    }
    
    @Override
    public void disconnect() {
    	serverClose = true;
    	if (sc.isOpen()) {
    		sc.close();
    	}
    }
    
    public void write(byte[] msg) {
    	write(msg, !requestHandler.isRepeatableRequest());
    }
    
    
    /**
     * 
     * @exception 
     */
    public void write(byte[] msg, final boolean forceDisconnectAfterWrite) {
    	lock.lock();
    	try {
    		final Client client = this;
    		if (msg == null) {
    			msg = new byte[0];
    		}
    		if (isConnected()) {
    			
    			if (writeTimeoutMillis < 0) {
    				BufUtils.write(sc, msg, new ChannelFutureListener() {
    					
    					public void operationComplete(ChannelFuture future) throws Exception {
    						
    						if (!future.isSuccess()) {
    							requestHandler.onExceptionRequestIO(client, future.cause());
            				}
            				
    						if (!requestHandler.isRepeatableRequest() || forceDisconnectAfterWrite) {
            					disconnect();
            				}
    					}
    				});
    			} else {
    				if (!BufUtils.write(sc, msg, writeTimeoutMillis)) {
    					requestHandler.onExceptionRequestIO(client, new ClientAbortException(client));
    				}
    				
    				if (!requestHandler.isRepeatableRequest() || forceDisconnectAfterWrite) {
    					disconnect();
    				}
    			}
    			
        	} else {
        		if (!serverClose && !sc.isOpen()) {
        			throw new ClientAbortException(client);
        		}
        	}
    		
    		if (!requestHandler.isRepeatableRequest() || forceDisconnectAfterWrite) {
	    		serverClose = true;
	    	}
    		
    		//TODO -> 연결지향에서.. 첫 리퀘스트 정상이고.. 그 다음 데이터 읽는데 오류 나면.. 첫 리퀘스트 응답주고..닫아야 하나... 다음에..
	    	
    	} finally {
			lock.unlock();
		}
    }
}
