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
	
	private final BlockingQueue<Channel> connectionQueue;
	//큐로만든이유.. 이미 접속해 있는 사람부터 우선권을 주기 위해..했음...그런데 잘못되었네..
	
	private final int maxConnectionSize;
	 
	private ConnectionRejectListener connectionRejectListener;
	
	private final static String DEFAULT_REJECT_MESSAGE = "server is too busy";
	
	public ConnectionManagerHandler(int maxConnectionSize) {
		this.maxConnectionSize = maxConnectionSize;
		this.connectionQueue = new LinkedBlockingQueue<Channel>(maxConnectionSize);
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
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("접속 channelRegistered");
		if (connectionQueue.offer(ctx.channel())) {
			logger.debug("connected..  {}/{}", connectionQueue.remainingCapacity(), maxConnectionSize);

			super.channelRegistered(ctx);
		} else {
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
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("접속 channelActive");
		if (connectionQueue.contains(ctx.channel())) {
			super.channelActive(ctx);
		}
	}
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("접속 channelRead");
		if (connectionQueue.contains(ctx.channel())) {
			super.channelRead(ctx, msg);
		}
	}
	
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("접속 channelUnregistered");
		if (connectionQueue.remove(ctx.channel())) {
			logger.debug("disconnected ..  {}/{}", connectionQueue.remainingCapacity(), maxConnectionSize);
		}
		super.channelUnregistered(ctx);
	}
	
	
}