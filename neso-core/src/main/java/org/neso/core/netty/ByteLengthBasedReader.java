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

	/**
	 * 읽어야 할 바이트 길이 조회
	 * 
	 * @return
	 */
	public int getToReadBytes();
	    
	/**
	 * 
	 * @param readedBuf
	 * @return boolean 한 사이클이 완료되었는지 (이상하지만...readtimeHandler책임을 분산시키는게 이상해서.. 두지 않으려면 )
	 * @throws Exception
	 */
	public boolean onRead(ByteBuf readedBuf) throws Exception;
	
	/**
	 * 읽기가 종료되었는지
	 * @return
	 */
	public boolean isClose();
	
	public void destroy();
	
	public void onReadException(Throwable th);
}
