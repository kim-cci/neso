package org.neso.core.netty;

import static io.netty.util.internal.StringUtil.NEWLINE;

import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class HeadBodyReader implements ByteLengthBasedReader {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final private RequestHandler requestHandler;
	
	final private Client client;
	
	private OperableHeadBodyRequest currentRequest;
	
	private boolean readable = true;
	
	
	public HeadBodyReader(RequestHandler requestHandler, Client client) {
    	this.requestHandler = requestHandler;
    	this.client = client;
    	this.currentRequest =  requestHandler.getRequestFactory().newHeadBodyRequest(client, requestHandler);
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
    	if (!readable) {
    		return 0;
    	}
    	
    	int toReadBytes = 0;
    	if (!currentRequest.isReadedHead()) {
    		int headLength = requestHandler.getHeadLength();
    		if (headLength < 1) {
    			throw new RuntimeException("Header length cannot be zero or a negative number ");
    		}
    		toReadBytes = headLength;
		} else if (!currentRequest.isReadedBody()) {
			toReadBytes = requestHandler.getBodyLength(currentRequest);
		}
    	
    	return toReadBytes;
    }
    
    private void log(String eventName, ByteBuf readedBuf) {
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
    public boolean onRead(ByteBuf readedBuf) throws Exception {

		if (!currentRequest.isReadedHead()) {
			
			//if log
			log("HEADER RECEIVED", readedBuf);

			currentRequest.setHeadBytes(readedBuf);
			
			if (getToReadByte() == 0) { 
				/**
				 * 바디가 0인 경우, 더 읽어야 할 필요가 없다면.. request read complete 상태로 만들기 위해
				 **/
				currentRequest.setBodyBytes(Unpooled.directBuffer(0));
			}
    		
    		return false; //헤더 읽기 완료.. 바디를 읽어야 함. false
		} else if (!currentRequest.isReadedBody()) {
			//if log
			log("BODY RECEIVED", readedBuf);
			
			currentRequest.setBodyBytes(readedBuf);
			
			requestHandler.onRequest(client, currentRequest);
    		
    		if (requestHandler.getRequestFactory().isRepeatableReceiveRequest()) {
    			this.currentRequest = requestHandler.getRequestFactory().newHeadBodyRequest(client, requestHandler);
			} else {
				readable = false;
			}
        	
        	return true;
		} else {
			throw new RuntimeException(".... not");
		}
    }
    
    
	@Override
	public void onReadException(Throwable th) {

    	try {
    		requestHandler.onExceptionRead(client, th);
    	}catch (Exception e) {
			logger.error("occurred exception.. requestHandler's onExceptionRead", e);
		}
	}
}
