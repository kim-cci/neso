package org.neso.core.netty;

import io.netty.buffer.ByteBuf;

public interface ByteLengthBasedReader {
	
	public void init();
	    
	public int getToReadByte();
	    
	public boolean onRead(ByteBuf readedBuf) throws Exception;
	
	public void close();
	
	public void onReadException(Throwable th);
}
