package org.neso.core.server;

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

/**
 * 연결형 서버
 * 
 *  접속 -> 요청 -> 응답  -> [요청 -> 응답.....]-> 접속종료
 *  
 *  readTimeoutMillisOnReadStatus, request 상태에서 readTimeout
 *  
 *   * TODO BOOTSTRAP 옵션도 건들 수 있게...언제 다하냐..
 */
@Deprecated
public class ConnectionOrientedAsyncServer extends AbstractServer {

	private int maxConnections = -1;
	private int readTimeoutMillisOnRead = -1;
	private int writeTimeoutMillis = - 1;
	
	private LogLevel logLevel = null;
    private ConnectionManagerHandler connectionManagerHandler = null;
    
    
    public ConnectionOrientedAsyncServer(RequestHandler requestHandler, int port) {
    	super(requestHandler, port);
	}
    
    public ConnectionOrientedAsyncServer(RequestHandler requestHandler, int port, int ioThreads) {
    	super(requestHandler, port, ioThreads);
	}
    
    public ConnectionOrientedAsyncServer maxConnections(int maxConnections) {
    	this.maxConnections = maxConnections;
    	return this;
    }

    public ConnectionOrientedAsyncServer readTimeoutSecOnReadStatus(int readTimeoutMillisOnRead) {
    	this.readTimeoutMillisOnRead = readTimeoutMillisOnRead;
    	return this;
    }
    
    public ConnectionOrientedAsyncServer writeTimeoutMillis(int writeTimeoutMillis) {
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	return this;
    }

    public ConnectionOrientedAsyncServer loggingLevel(LogLevel level) {
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
 
		ClientAgent clientAgent = new ClientAgent(sc, ServerContext.context(getPort(), maxConnections, getRequestHandler()), writeTimeoutMillis, true, 0);
		
		
		cp.addLast(new ByteLengthBasedInboundHandler(clientAgent.getReader(), readTimeoutMillisOnRead)); //4. READ 처리
		
	}
	
	@Override
	protected void option(ServerBootstrap serverBootstrap) {
		
	}
}
