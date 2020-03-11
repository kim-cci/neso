package org.neso.core.netty;

import static io.netty.util.internal.StringUtil.NEWLINE;


import org.neso.core.request.Client;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.neso.core.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class HeadBodyReader implements ByteLengthBasedReader {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	 
	final private Client client;
	
	final private ServerContext serverContext;
	
	private OperableHeadBodyRequest currentRequest;
	
	private boolean readable = true;

	
	public HeadBodyReader(Client client, ServerContext serverContext) {
		this.client = client;
		this.serverContext = serverContext;
		
    	init();
	}
	
	@Override
	public void init() {
		this.currentRequest = serverContext.requestFactory().newHeadBodyRequest(client);
		serverContext.requestHandler().onConnect(client);
	}

	@Override
	public void destroy() {
		serverContext.requestHandler().onDisconnect(client);
	}
    
    @Override
    public boolean isClose() {
    	return !readable;
    }
	
    @Override
    public int getToReadBytes() {
    	if (isClose()) {
    		return 0; //throw new RuntimeException("reader is closed...");
    	}
    	
    	if (!currentRequest.isReadedHead()) {
    		int headLength = serverContext.requestHandler().getHeadLength();
    		if (headLength < 1) {
    			throw new RuntimeException("Header length cannot be zero or a negative number ");
    		}
    		return headLength;
    		
		} else { //if (!currentRequest.isReadedBody()) 
			
			int bodyLength = serverContext.requestHandler().getBodyLength(currentRequest);
			
			if (serverContext.options().getMaxRequestBodyLength() > 0) {
				if (serverContext.options().getMaxRequestBodyLength() < bodyLength) {
					throw new RuntimeException("Too long body length..");
				}
			}
					
			return bodyLength;
		}
    }

    @Override
    public boolean onRead(ByteBuf readedBuf) throws Exception {

		if (!currentRequest.isReadedHead()) {
			
			if (serverContext.options().isInoutLogging()) {
				log("HEADER RECEIVED", readedBuf);
			}
			
			if (getToReadBytes() != readedBuf.capacity()) {
				throw new RuntimeException("it's different expected bytes");
			}
			
			currentRequest.setHeadBytes(readedBuf);
			
			if (getToReadBytes() == 0) {
                /**
                 * 바디가 0인 경우, 더 읽어야 할 필요가 없다면.. request 
                 **/
				return onRead(Unpooled.directBuffer(0));
			}
			return false;
		} else { //if (!currentRequest.isReadedBody()) 
			
			if (serverContext.options().isInoutLogging()) {
				log("BODY RECEIVED", readedBuf);
			}
			
			if (getToReadBytes() != readedBuf.capacity()) {
				throw new RuntimeException("it's different expected bytes");
			}
			
			currentRequest.setBodyBytes(readedBuf);
			
			serverContext.requestHandler().onRequest(client, currentRequest);
    		
    		if (serverContext.options().isConnectionOriented()) {
    			this.currentRequest = serverContext.requestFactory().newHeadBodyRequest(client);
			} else {
				readable = false;
			}
        	
    		return true;
		}
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
	public void onReadException(Throwable th) {

    	try {
    		serverContext.requestHandler().onExceptionRead(client, th);
    	}catch (Exception e) {
			logger.error("occurred exception.. requestHandler's onExceptionRead", e);
		}
	}
}
