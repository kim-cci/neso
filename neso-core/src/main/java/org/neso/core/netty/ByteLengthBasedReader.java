package org.neso.core.netty;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @See {@link ByteLengthBasedInboundHandler}
 * 
 * 1.getToReadByte
 * 2.onRead(toReadBuf) 
 * 
 */
public interface ByteLengthBasedReader {
	
	public void init();
	    
	public int getToReadByte();
	    
	public boolean onRead(ByteBuf readedBuf) throws Exception;
	
	public void close();
	
	public void onReadException(Throwable th);
}
