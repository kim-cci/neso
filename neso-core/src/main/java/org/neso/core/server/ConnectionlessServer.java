package org.neso.core.server;

import java.util.concurrent.TimeUnit;

import org.neso.core.netty.ByteLengthBasedInboundHandler;
import org.neso.core.netty.ClientAgent;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.support.ConnectionManagerHandler;
import org.neso.core.support.ConnectionRejectListener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 비연결형 서버
 * 
 * 접속 -> 요청 -> 응답 -> 접속종료
 * 
 * maxTaskThreads : request 처리 스레드 최대 수
 * maxConnections : 최대 접속 허용 수
 * readTimeoutMillis 
 * writeTimeoutMillis 
 * 
 * TODO BOOTSTRAP 옵션도 건들 수 있게...언제 다하냐..
 */

@Deprecated
public class ConnectionlessServer extends AbstractServer {

    
    private int readTimeoutMillis = -1;
    private int maxConnections = -1;
    private int writeTimeoutMillis = - 1;
    
    private LogLevel logLevel = null;
   
    private ConnectionManagerHandler connectionManagerHandler = null;
    
    public ConnectionlessServer(RequestHandler requestHandler, int port) {
    	super(requestHandler, port);
	}
    
    public ConnectionlessServer(RequestHandler requestHandler, int port, int ioThreads) {
    	super(requestHandler, port, ioThreads);
	}
    
    public ConnectionlessServer readTimeoutMillis(int readTimeoutMillis) {
    	this.readTimeoutMillis = readTimeoutMillis;
    	return this;
    }
    
    public ConnectionlessServer maxConnections(int maxConnections) {
    	this.maxConnections = maxConnections;
    	return this;
    }

    public ConnectionlessServer writeTimeoutMillis(int writeTimeoutMillis) {
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	return this;
    }
    
    public ConnectionlessServer loggingLevel(LogLevel level) {
    	this.logLevel = level;
    	return this;
    }
    
	
	@Override
	protected void initializerServerStart() {
	

		ConnectionManagerHandler connectionManagerHandler = maxConnections > 0 ? new ConnectionManagerHandler(maxConnections) : null;
		if (getRequestHandler() instanceof ConnectionRejectListener) {
			connectionManagerHandler.setConnectionRejectListener((ConnectionRejectListener) getRequestHandler());
		}
		
		if (!getRequestHandler().getRequestTaskPool().isAsyncResponse()) {
			ioThreads(getRequestHandler().getRequestTaskPool().getMaxThreads() + 10);
		}
	}
    
	@Override
	protected void initializerAccept(SocketChannel sc) {
		
		ChannelPipeline cp = sc.pipeline();
		if (connectionManagerHandler != null) {
			cp.addLast(connectionManagerHandler); //1.접속 제한
		}
		
		if (logLevel != null) {
			cp.addLast(new LoggingHandler(logLevel));	//2.로깅
		}
		
		
		if (readTimeoutMillis > 0) {
			cp.addLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));//3.리드 타임아웃
		}
		
	
		ClientAgent clientAgent = new ClientAgent(sc, ServerContext.context(getPort(), maxConnections, getRequestHandler()), writeTimeoutMillis);
		
		cp.addLast(new ByteLengthBasedInboundHandler(clientAgent.getByteLengthBasedReader(), -1)); //4. READ 처리
		
	}

	@Override
	protected void option(ServerBootstrap serverBootstrap) {
	}
}
