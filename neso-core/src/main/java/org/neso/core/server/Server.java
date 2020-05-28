package org.neso.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.lang.reflect.Constructor;

import org.neso.core.netty.ByteLengthBasedInboundHandler;
import org.neso.core.netty.ClientAgent;
import org.neso.core.request.factory.InMemoryRequestFactory;
import org.neso.core.request.factory.RequestFactory;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.handler.task.RequestExecutor;
import org.neso.core.request.handler.task.BasicRequestThreadExecutor;
import org.neso.core.support.ConnectionManagerHandler;
import org.neso.core.support.ConnectionRejectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final private int port;
    final private RequestHandler requestHandler;
    
    private int maxConnections = -1;
    
	//private int readTimeoutMillis = -1;		//io스레드가 슬립(작업중)이라면 스케쥴이 실행이 안되어서 타임아웃이 안먹힌다. 먹힐려면.. 그 다음 파이프라인이 새로운 스레드그룹이어야 하는데...이 구조는 아니니...
	private int readTimeoutMillisOnRead = 5000;
	
	private int writeTimeoutMillis = 2000;
	
	private int maxRequestBodyLength = -1;
	
	private LogLevel pipelineLogLevel = null;
	
	private boolean inoutLogging = false;
	
	private boolean connectionOriented = false;
	
    private int ioThreads = 0;	//0일 경우 네티 전략 따름 
    
    private int maxRequests = 100;
    
    private Class<? extends RequestExecutor> requestExecutorType = BasicRequestThreadExecutor.class;
	
    private ConnectionManagerHandler connectionManagerHandler;
    
    private ServerContext context;
    
    public Server(RequestHandler requestHandler, int port) {
    	this.requestHandler = requestHandler;
    	this.port = port;
	}

    
    public Server maxRequests(int maxRequests) {
    	this.maxRequests = maxRequests;
    	return this;
    }

    public Server readTimeout(int readTimeoutMillis) {
    	this.readTimeoutMillisOnRead = readTimeoutMillis;
    	return this;
    }
    
    public Server maxConnections(int maxConnections) {
    	this.maxConnections = maxConnections;
    	return this;
    }
    
    public Server maxRequestBodyLength(int maxRequestBodyLength) {
    	this.maxRequestBodyLength = maxRequestBodyLength;
    	return this;
    }

    public Server writeTimeoutMillis(int writeTimeoutMillis) {
    	
    	if (writeTimeoutMillis < 0) {
    		throw new RuntimeException("writeTimeoutMillis is bigger than zero");
    	}
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	return this;
    }
    
    public Server pipeLineLogLevel(LogLevel level) {
    	this.pipelineLogLevel = level;
    	return this;
    }
    
    public Server inoutLogging(boolean inoutLogging) {
    	this.inoutLogging = inoutLogging;
    	return this;
    }
    
    public Server connectionOriented() {
    	this.connectionOriented = true;
    	return this;
    }
    
    public Server requestExecutorType(Class<? extends RequestExecutor> executorClz) {
    	this.requestExecutorType = executorClz;
    	return this;
    }
    
    @Deprecated
    public Server requestExecutorType(Class<? extends RequestExecutor> executorClz, Class<?>... parameterTypes) {
    	this.requestExecutorType = executorClz;
    	return this;
    }

    
    public void start() {
    	
    	initializerServerStart();
    	
    	int bossThreas = Runtime.getRuntime().availableProcessors();
    	
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreas);
        EventLoopGroup workerGroup = new NioEventLoopGroup(ioThreads);//connectionManager.getMaxConnectionSize() + 1
        
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
            
            sbs.option(ChannelOption.SO_BACKLOG, bossThreas * 20);
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
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
    			@Override
    			public void run() {
    				cf.channel().close();
    			}
    		});

            cf.channel().closeFuture().sync().addListener(new GenericFutureListener<ChannelFuture>() {
                public void operationComplete(ChannelFuture future) throws Exception {
                	
                	context.requestTaskExecutor().shutdown();
                	
                    if (future.isSuccess()) {
                        logger.info("socket server shutdown .... bind port={}", port);
                    } else {
                        logger.info("socket server shutdown fail !!! bind port={}", port);
                    }
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
    	
    	RequestExecutor requestTaskExecutor = createRequestTaskExecutor();
    	//TODO requestTaskExecutor 로그 출력
    	
		if (requestTaskExecutor.isRunIoThread()) {
			ioThreads = requestTaskExecutor.getMaxRequets() + 1; //io스레드가 request처리 동시실행숫자보다 작으면 io스레드에서 병목이 발생하므로.. io스레드가 무조건 커야한다.
		} else {
			//io thread는 네티 전략 따름 -> 0
		}
		
    	RequestFactory requestFactory = new InMemoryRequestFactory(); //일단 메모리만 제공
    	
		ConnectionManagerHandler connectionManagerHandler = maxConnections > 0 ? new ConnectionManagerHandler(maxConnections) : null;
		if (connectionManagerHandler != null && requestHandler instanceof ConnectionRejectListener) {
			connectionManagerHandler.setConnectionRejectListener((ConnectionRejectListener) requestHandler);
		}
		
    	ServerOptions options = new ServerOptions(connectionOriented, maxRequests, readTimeoutMillisOnRead, writeTimeoutMillis, maxConnections, maxRequestBodyLength, inoutLogging);
    	
    	//TODO 서버옵션 로그 출력
    	this.context = new ServerContext(port, requestHandler, requestFactory, requestTaskExecutor, options, connectionManagerHandler);
    }
    
    private RequestExecutor createRequestTaskExecutor() {
    	try {
    
        	//Constructor<? extends RequestTaskExecutor> cons = requestExecutorType.getConstructor(new Class[]{int.class});
    		Constructor<? extends RequestExecutor> cons = requestExecutorType.getConstructor();
        	RequestExecutor ins = cons.newInstance();
        	ins.init(maxRequests);
        	return ins;
    	} catch (Exception e) {
    		throw new RuntimeException("requestTaskExecutor create error", e);
    	}
    }
    
    protected void initializerAccept(SocketChannel sc) {
    	

    	ClientAgent clientAgent = new ClientAgent(sc, context);
    	
		ChannelPipeline cp = sc.pipeline();
		if (connectionManagerHandler != null) {
			cp.addLast(connectionManagerHandler); //1.접속 제한
		}
		
		if (pipelineLogLevel != null) {
			cp.addLast(new LoggingHandler(pipelineLogLevel));	//2.로깅
		}
		
		//if (readTimeoutMillis > 0) {
		//	cp.addLast(new AsyncCloseReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS, clientAgent.getReader()));//3.리드 타임아웃
		//}

		cp.addLast(ByteLengthBasedInboundHandler.class.getSimpleName(), new ByteLengthBasedInboundHandler(clientAgent.getReader(), readTimeoutMillisOnRead)); //4. READ 처리
    }
    
    protected void option(ServerBootstrap sb) {
    	
    }
}
