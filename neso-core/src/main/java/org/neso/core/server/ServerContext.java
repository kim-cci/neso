package org.neso.core.server;

import org.neso.core.request.factory.RequestFactory;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.handler.task.RequestTaskExecutor;
import org.neso.core.support.ConnectionManager;

public class ServerContext {
	
	final private int port;
	final private RequestHandler requestHandler;
	final private RequestFactory requestFactory;
	final private RequestTaskExecutor requestTaskExecutor;
	final private ServerOptions options;
	final private ConnectionManager connectionManager;
	
	public ServerContext(int port, RequestHandler requestHandler, RequestFactory requestFactory, RequestTaskExecutor requestTaskExecutor, ServerOptions options, ConnectionManager connectionManager) {
		this.port = port;
		this.requestHandler = requestHandler;
		this.requestFactory = requestFactory;
		this.requestTaskExecutor = requestTaskExecutor;
		this.options = options;
		this.connectionManager = connectionManager;
	}
	
	public int port() {
		return this.port;
	}
	
	public RequestHandler requestHandler() {
		return this.requestHandler;
	}
	
	public RequestFactory requestFactory() {
		return this.requestFactory;
	}
	
	public RequestTaskExecutor requestTaskExecutor() {
		return this.requestTaskExecutor;
	}
	
	public ServerOptions options() {
		return this.options;
	}
	
	public ConnectionManager connectionManager() {
		return this.connectionManager;
	}
}
