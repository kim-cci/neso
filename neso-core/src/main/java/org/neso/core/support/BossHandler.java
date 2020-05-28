package org.neso.core.support;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.logging.LoggingHandler;

@Sharable //보스 스레드는 두 개 이상일 수 있음
public class BossHandler extends LoggingHandler implements ConnectionManager {

	private final int maxConnections;
	private AtomicInteger currentConnections = new AtomicInteger(0);
	
	private ConnectionRejectListener connectionRejectListener;
	
	private final static String DEFAULT_REJECT_MESSAGE = "server is too busy";
	
	public BossHandler(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	 
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	super.channelRead(ctx, msg);
    	
    	if (maxConnections > -1) {
    		if (currentConnections.incrementAndGet() > maxConnections) {
    			logger.debug("connected..  {}/{}", currentConnections.get(), maxConnections);
    			
    			
    		} else {
    			
    			currentConnections.decrementAndGet();
    			//접속 거절
    			logger.debug("connected...reject!!");

    			byte[] rejectMessage = DEFAULT_REJECT_MESSAGE.getBytes();
    			if (connectionRejectListener != null) {
    				try {
    					String remoteAddr = null;
    					if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
    			        	InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
    			        	remoteAddr = addr.getHostName();
    			    	} else {
    			    		remoteAddr = ctx.channel().remoteAddress().toString();
    			    	}
    					
    					rejectMessage = connectionRejectListener.onConnectionReject(getMaxConnectionSize() , remoteAddr);
    				} catch (Exception e) {
    					logger.error("occurred connectionRejectListner's onConnectionReject", e);
    				}
    			}
    			if (rejectMessage == null) {
    				ctx.close();
    			} else {
    				ByteBuf buf = ctx.alloc().buffer(rejectMessage.length);
    				buf.writeBytes(rejectMessage);
    				ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    			}
    		}
    	}
    	
    }
	
	@Override
	public int getCurruntConnectionSize() {
		// TODO Auto-generated method stub
		return currentConnections.get();
	}
	
	@Override
	public int getMaxConnectionSize() {
		// TODO Auto-generated method stub
		return maxConnections;
	}
}
