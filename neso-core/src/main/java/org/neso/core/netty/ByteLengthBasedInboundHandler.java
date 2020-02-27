package org.neso.core.netty;

import java.util.concurrent.TimeUnit;

import org.neso.core.exception.OverReadBytesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


/**
 * byte array 길이 기반으로 데이터를 읽는 ChannelInboundHandlerAdapter.
 * 
 * @see ByteLengthBasedReader
 * 
 * 1.getToReadByte -> 읽어야할 바이트 수를 clientAgent로부터 획득, 0이면 종료
 * 2.onRead -> 읽어어야 바이트를 다 읽으면 clientAgent로 bytebuf 전달
 * 
 * 1,2,3 반복...
 */
public final class ByteLengthBasedInboundHandler extends ChannelInboundHandlerAdapter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    final private ByteLengthBasedReader reader;
    
    final private int readTimeoutMillisOnRead;
	
    private ByteBuf toReadBuf;
    
    public ByteLengthBasedInboundHandler(ByteLengthBasedReader reader) {
    	this(reader, -1);
	}
    
    public ByteLengthBasedInboundHandler(ByteLengthBasedReader reader,  int readTimeoutMillisOnRead) {
    	this.reader = reader;
    	this.readTimeoutMillisOnRead = readTimeoutMillisOnRead;
	}

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    	int toReadBytes = reader.getToReadByte();
    	if (toReadBytes < 0) {
    		throw new RuntimeException("cant read ...");
    	}
    	toReadBuf = ctx.alloc().buffer(toReadBytes);
    }
    

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
		try {
			if (msg instanceof ByteBuf) {
				ByteBuf buf = (ByteBuf) msg;
		        
		    	while (buf.isReadable()) {
		    		
		    		if (toReadBuf.writableBytes() == 0) {
		    			byte[] overBytes = BufUtils.copyToByteArray(buf);
    		    		throw new OverReadBytesException(overBytes);
		    		}
		    		
		    		if (isCompleteReadBuf(buf, toReadBuf)) {

		    			if (reader.onRead(toReadBuf.copy())){
		    				//request가 읽기가 다 끝났다면..
		    				removeReadTimeoutHandler(ctx);
		    				
		    			} else {
		    				//request의 리드가 아직 남았다면..
		    				addReadTimeoutHandler(ctx);
		    			}
			    			 
                    	int toReadBytes = reader.getToReadByte();
                    	logger.debug("toReadBytes length => {}", toReadBytes);
                    	toReadBuf.clear();
                		toReadBuf.capacity(toReadBytes);
        
	                    
		    		} else {
		    			//입력받은 byte가 부족하다면.. toReadBuf가 아직 덜 찼다면...
		    			addReadTimeoutHandler(ctx);
		    		}
		    		

		    	}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
    }
    
    private void addReadTimeoutHandler(ChannelHandlerContext ctx) {
    	if (readTimeoutMillisOnRead > 0 && ctx.channel().pipeline().get("readTimeoutOnReadHandler") == null) {
    		ctx.channel().pipeline().addBefore("ByteLengthBasedInboundHandler", "readTimeoutOnReadHandler", new AsyncCloseReadTimeoutHandler(readTimeoutMillisOnRead, TimeUnit.MILLISECONDS, reader));
		}
    }
    
    private void removeReadTimeoutHandler(ChannelHandlerContext ctx) {
    	if (readTimeoutMillisOnRead > 0 && ctx.channel().pipeline().get("readTimeoutOnReadHandler") != null) {
			ctx.channel().pipeline().remove("readTimeoutOnReadHandler");
		}
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	if (toReadBuf != null && toReadBuf.refCnt() > 0) {
    		ReferenceCountUtil.release(toReadBuf);
    	}
    	
    	reader.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {	
    	reader.onReadException(cause);
    }
    
    private boolean isCompleteReadBuf(ByteBuf fromBuf, ByteBuf toBuf) {

    	int readLength = toBuf.capacity();
        if (readLength < 1) {
            return true;
        }
        if (fromBuf.isReadable()) {
            int readlength = toBuf.writableBytes() < fromBuf.readableBytes() ? toBuf.writableBytes() :fromBuf.readableBytes();
            fromBuf.readBytes(toBuf, readlength);
            return toBuf.readableBytes() >= readLength;
        } else {
            return false;
        }
    }
}