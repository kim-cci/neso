package org.neso.core.server;

import org.neso.core.request.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

@Deprecated
abstract class AbstractServer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
 

    final private int port;
    final private RequestHandler requestHandler;
     
    private int workThreadCnt;
    
    public AbstractServer(RequestHandler requestHandler, int port) {
    	this(requestHandler, port, 0);
	}
    
    public AbstractServer(RequestHandler requestHandler, int port, int ioThreads) {
    	this.requestHandler = requestHandler;
    	this.port = port;
    	
    	if (workThreadCnt < 0) {
    		workThreadCnt = 0;
    	}
    	this.workThreadCnt = ioThreads;
	}
    
    protected RequestHandler getRequestHandler() {
		return requestHandler;
	}
    
    public int getPort() {
    	return port;
    }
    
 
    protected void ioThreads(int ioThreads) {
    	this.workThreadCnt = ioThreads;
    }
    
    public void start() {
    	
    	initializerServerStart();
    	
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workThreadCnt);//connectionManager.getMaxConnectionSize() + 1
        
        try {
            ServerBootstrap sbs = new ServerBootstrap(); 
            sbs.group(bossGroup, workerGroup).
            channel(NioServerSocketChannel.class);

            sbs.childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 
                	 initializerAccept(ch);
                 }
             });
            
            option(sbs);
            
            final ChannelFuture cf = sbs.bind(port).sync().addListener(new GenericFutureListener<ChannelFuture>() {
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("socket server started !!  bind port={}", port);
                    } else {
                        logger.info("socket server start failed !!");
                    }
                }
            });
            
            cf.channel().closeFuture().sync().addListener(new GenericFutureListener<ChannelFuture>() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("socket server shutdown .... bind port={}", port);
                    } else {
                        logger.info("socket server shutdown fail !!! bind port={}", port);
                    }
                } 
            });
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
    			@Override
    			public void run() {
    				cf.channel().close();
    			}
    		});
        } catch (Exception e) {
        	throw new RuntimeException("server start fail", e);
            
        } finally {
            try {
                workerGroup.shutdownGracefully().get();
                bossGroup.shutdownGracefully().get();
            } catch (Exception e) {
            	throw new RuntimeException(e);
            }           
        }
    }
    
    protected abstract void initializerAccept(SocketChannel ch);
    
    protected abstract void initializerServerStart();
    
    protected abstract void option(ServerBootstrap sb);
}
