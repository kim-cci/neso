package org.neso.core.netty;

import io.netty.buffer.ByteBuf;

public interface ByteBasedWriter {

	public void write(byte[] bytes);
	
	public void write(ByteBuf buf);
	
	public void flush();
	
	public void close();
	
	public void closeAndDisconnect();
}
