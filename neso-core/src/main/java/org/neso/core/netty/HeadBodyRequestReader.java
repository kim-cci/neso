package org.neso.core.netty;

import org.neso.core.request.Client;
import org.neso.core.request.factory.RequestFactory;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class HeadBodyRequestReader implements ByteLengthBasedReader {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final private RequestHandler requestHandler;
	
	final private Client client;
	
	private OperableHeadBodyRequest currentRequest;
	
	private boolean makeAbleRequest = true;
	
	private RequestFactory requestFactory;
	
	public HeadBodyRequestReader(RequestHandler requestHandler, RequestFactory requestFactory, Client client) {
    	this.requestHandler = requestHandler;
    	this.requestFactory = requestFactory;
    	this.client = client;
    	this.currentRequest =  requestFactory.newHeadBodyRequest(client, requestHandler);
    	init();
	}
	
	@Override
	public void init() {
		requestHandler.onConnect(client);
	}
	
	
	
	@Override
	public void close() {
		requestHandler.onDisConnect(client);
	}
    
    
    @Override
    public int getToReadByte() {
    	if (!makeAbleRequest) {
    		return 0;
    	}
    	
    	if (!currentRequest.isReadedHead()) {
    		int headLength = requestHandler.getHeadLength();
    		if (headLength < 1) {
    			throw new RuntimeException("Header length cannot be zero or a negative number ");
    		}
			return headLength;
		} else if (!currentRequest.isReadedBody()) {
			return requestHandler.getBodyLength(currentRequest);
		} else {
			return 0;
		}
    }
    
    @Override
    public boolean onRead(ByteBuf readedBuf) throws Exception {
    	

		if (!currentRequest.isReadedHead()) {
			currentRequest.setHeadBytes(readedBuf);
			
			if (getToReadByte() == 0) { 
				/**
				 * 바디가 0인 경우, 더 읽어야 할 필요가 없다면.. request read complete 상태로 만들기 위해
				 **/
				currentRequest.setBodyBytes(Unpooled.directBuffer(0));
			}
			
		} else if (!currentRequest.isReadedBody()) {
			currentRequest.setBodyBytes(readedBuf);
			
		} else {
			throw new RuntimeException(".... not");
		}
	    
    	
    	if (currentRequest.isReadedBody()) {
    		//실행 처리
    		
    		requestHandler.onRequest(client, currentRequest);
    		
    		if (requestHandler.isRepeatableRequest()) {
    			this.currentRequest = requestFactory.newHeadBodyRequest(client, requestHandler);
			} else {
				makeAbleRequest = false;
			}
        	
        	return true;
    	} else {
    		//헤더 읽기 완료.. 바디를 읽어야 함.
    		return false;
    	}
    }
    
    
	@Override
	public void onReadException(Throwable th) {

    	try {
    		requestHandler.onExceptionRequestIO(client, th);
    	}catch (Exception e) {
			logger.error("occurred exception.. serverHandler's exceptionCaughtRequestIO", e);
		}
	}
}
