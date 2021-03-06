package org.neso.core.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ConnectionManagerHandler extends ChannelInboundHandlerAdapter implements ConnectionManager {
 
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//큐로 만든 이유.. 기억이 안난다.. atomic integer로 다시 바꿀까... 
	private final BlockingQueue<Channel> connectionQueue; 
	
	private final int maxConnectionSize;
	 
	private ConnectionRejectListener connectionRejectListener;
	
	private final static String DEFAULT_REJECT_MESSAGE = "server is too busy";
	
	public ConnectionManagerHandler(int maxConnectionSize) {
		this.maxConnectionSize = maxConnectionSize;
		this.connectionQueue = new LinkedBlockingQueue<Channel>(maxConnectionSize == -1 ? Integer.MAX_VALUE : maxConnectionSize);
	}
	
	public void setConnectionRejectListener(ConnectionRejectListener connectionRejectListener) {
		this.connectionRejectListener = connectionRejectListener;
	}
	
	@Override
	public int getCurruntConnectionSize() {
		return this.connectionQueue.size();
	}
	
	@Override
	public int getMaxConnectionSize() {
		return maxConnectionSize;
	}

	
 
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (connectionQueue.offer(ctx.channel())) {
			logger.debug("connected..  {}", ctx.channel().toString());

			super.channelActive(ctx);
		} else {
			//접속 거절
			logger.debug("connected...reject!! {}", ctx.channel().toString());

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
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (connectionQueue.contains(ctx.channel())) {
			super.channelRead(ctx, msg);
		}
	}
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (connectionQueue.remove(ctx.channel())) {
			logger.debug("disconnected .. {}", ctx.channel().toString());
		}
		super.channelInactive(ctx);
	}
}