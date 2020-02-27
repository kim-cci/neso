package org.neso.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

import org.neso.core.netty.AsyncCloseReadTimeoutHandler;
import org.neso.core.netty.ByteLengthBasedInboundHandler;
import org.neso.core.netty.ClientAgent;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.support.ConnectionManagerHandler;
import org.neso.core.support.ConnectionRejectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final private int port;
    final private RequestHandler requestHandler;
    
    private int maxConnections = -1;
    
	private int readTimeoutMillis = -1;
	private int readTimeoutMillisOnRead = -1;
	
	private int writeTimeoutMillis = 2000;
	
	
	private LogLevel logLevel = null;
	   
	private ConnectionManagerHandler connectionManagerHandler = null;
    
	 
    private int workThreadCnt = 0;	//0일 경우 core * 2
    
    private ServerContext context;
    
    public Server(RequestHandler requestHandler, int port) {
    	this.requestHandler = requestHandler;
    	this.port = port;
	}
     
    protected RequestHandler getRequestHandler() {
		return requestHandler;
	}
    
    public int getPort() {
    	return port;
    }
    
 
    protected Server ioThreads(int ioThreads) {
    	if (ioThreads < 0) {
    		ioThreads = 0;
    	}
    	this.workThreadCnt = ioThreads;
    	return this;
    }
    
    public Server readTimeoutMillis(int readTimeoutMillis) {
    	if (requestHandler.getRequestFactory().isRepeatableReceiveRequest()) {	//접속 유지형은 사용불가..
    		logger.warn("invalid option ");
    	} else {
    		this.readTimeoutMillis = readTimeoutMillis;
    	}
    	return this;
    }
    
    public Server readTimeoutMillisOnReadStatus(int readTimeoutMillisOnRead) {
    	this.readTimeoutMillisOnRead = readTimeoutMillisOnRead;
    	return this;
    }
    
    public Server maxConnections(int maxConnections) {
    	this.maxConnections = maxConnections;
    	return this;
    }

    public Server writeTimeoutMillis(int writeTimeoutMillis) {
    	
    	if (writeTimeoutMillis < 0) {
    		throw new RuntimeException("writeTimeoutMillis is bigger than zero");
    	}
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	return this;
    }
    
    public Server loggingLevel(LogLevel level) {
    	this.logLevel = level;
    	return this;
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
                        
                        requestHandler.init(context);
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

    protected void initializerServerStart() {
    	
    	this.context = ServerContext.context(getPort(), maxConnections, getRequestHandler());
    	
		ConnectionManagerHandler connectionManagerHandler = maxConnections > 0 ? new ConnectionManagerHandler(maxConnections) : null;
		if (getRequestHandler() instanceof ConnectionRejectListener) {
			connectionManagerHandler.setConnectionRejectListener((ConnectionRejectListener) getRequestHandler());
		}
		
		if (!getRequestHandler().getRequestTaskPool().isAsyncResponse()) {
			ioThreads(getRequestHandler().getRequestTaskPool().getMaxThreads() + 10);
		}
    }
    
    protected void initializerAccept(SocketChannel sc) {
    	
    	ClientAgent clientAgent = new ClientAgent(sc, context, writeTimeoutMillis);
    	
		ChannelPipeline cp = sc.pipeline();
		if (connectionManagerHandler != null) {
			cp.addLast(connectionManagerHandler); //1.접속 제한
		}
		
		if (logLevel != null) {
			cp.addLast(new LoggingHandler(logLevel));	//2.로깅
		}
		
		
		if (readTimeoutMillis > 0) {
			cp.addLast(new AsyncCloseReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS, clientAgent.getByteLengthBasedReader()));//3.리드 타임아웃
		}
		
	
		
		
		cp.addLast("ByteLengthBasedInboundHandler", new ByteLengthBasedInboundHandler(clientAgent.getByteLengthBasedReader(), readTimeoutMillisOnRead)); //4. READ 처리
    }
    
    protected void option(ServerBootstrap sb) {
    	
    }
}
