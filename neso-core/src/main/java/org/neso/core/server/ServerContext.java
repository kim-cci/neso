package org.neso.core.server;

import org.neso.core.request.handler.RequestHandler;

public class ServerContext {
	
	final int port;
	final int maxConnection;
	final RequestHandler requestHandler;
	
	
	private ServerContext(int port, int maxConnection, RequestHandler requestHandler) {
		this.port = port;
		this.maxConnection = maxConnection;
		this.requestHandler = requestHandler;
	}
	
	public static ServerContext context(int port, int maxConnection, RequestHandler requestHandler) {
		return new ServerContext(port, maxConnection, requestHandler);
	}
	
	public int getPort() {
		return this.port;
	}
	
	public RequestHandler getRequestHandler() {
		return this.requestHandler;
	}
	
	public int maxConnection() {
		return this.maxConnection;
	}
}
