package org.neso.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class BufUtils {
    
    public static byte[] copyToByteArray(ByteBuf buf) {
        byte[] temp = new byte[buf.readableBytes()];
        buf.readBytes(temp);
        return temp;
    }
    
    public static void writeAndClose(Channel channel, byte[] messageBytes) {
    	write(channel, messageBytes, ChannelFutureListener.CLOSE);
    }
    
    public static void write(Channel channel, byte[] messageBytes, final ChannelFutureListener afwrite) {
		final ByteBuf messageBuf = Unpooled.buffer(messageBytes.length);
		messageBuf.writeBytes(messageBytes);
		channel.writeAndFlush(messageBuf).addListener(afwrite);
		
    }
    
    public static boolean write(Channel channel, byte[] messageBytes, int timeoutMillis) {
		final ByteBuf messageBuf = Unpooled.buffer(messageBytes.length);
		messageBuf.writeBytes(messageBytes);
		try {
			return channel.writeAndFlush(messageBytes).await(timeoutMillis);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
