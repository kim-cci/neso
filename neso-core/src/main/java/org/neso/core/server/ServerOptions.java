package org.neso.core.server;

import org.neso.core.request.handler.task.BasicRequestThreadExecutor;
import org.neso.core.request.handler.task.RequestExecutor;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;

public abstract class ServerOptions implements ServerUI {
	
	private boolean connectionOriented = false;
	
	private int maxRequests = 100;
	
	private int readTimeoutMillisOnRead = 5000;
	
	private int writeTimeoutMillis = 2000;
	
	private int maxConnections = -1;
   
	private int maxRequestBodyLength = -1;
	
	private boolean inoutLogging = false;
	
	private LogLevel pipeLineLogLevel  = null;
	
	private Class<? extends RequestExecutor> requestExecutorType = BasicRequestThreadExecutor.class;
	
	
	public Class<? extends RequestExecutor> getRequestExecutorType() {
		return requestExecutorType;
	}
	
	public boolean isConnectionOriented() {
		return connectionOriented;
	}

	public int getMaxRequests() {
		return maxRequests;
	}
	
	public int getReadTimeoutMillisOnRead() {
		return readTimeoutMillisOnRead;
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public int getMaxRequestBodyLength() {
		return maxRequestBodyLength;
	}
	
	public boolean isInoutLogging() {
		return inoutLogging;
	}
	
	public LogLevel getPipeLineLogLevel() {
		return pipeLineLogLevel;
	}
	
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}

	
	
	
	public ServerOptions connectionOriented(boolean connectionOriented) {
		this.connectionOriented = connectionOriented;
		return this;
	}

	public ServerOptions maxRequests(int maxRequests) {
		this.maxRequests = maxRequests;
		return this;
	}

	public ServerOptions requestExecutorType(Class<? extends RequestExecutor> requestExecutorType) {
		this.requestExecutorType = requestExecutorType;
		return this;
	}
	
	public ServerOptions readTimeoutMillisOnRead(int readTimeoutMillisOnRead) {
		this.readTimeoutMillisOnRead = readTimeoutMillisOnRead;
		return this;
	}

	public ServerOptions writeTimeoutMillis(int writeTimeoutMillis) {
		if (writeTimeoutMillis < 0) {
	  		throw new RuntimeException("writeTimeoutMillis is bigger than zero");
	  	}
	  	this.writeTimeoutMillis = writeTimeoutMillis;
		return this;
	}

	public ServerOptions maxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

	public ServerOptions maxRequestBodyLength(int maxRequestBodyLength) {
		this.maxRequestBodyLength = maxRequestBodyLength;
		return this;
	}

	public ServerOptions inoutLogging(boolean inoutLogging) {
		this.inoutLogging = inoutLogging;
		return this;
	}
	
	public ServerOptions pipeLineLogLevel(LogLevel pipeLineLogLevel) {
		this.pipeLineLogLevel = pipeLineLogLevel;
		return this;
	}
	
    /**
 	@Deprecated
  	public Server requestExecutorType(Class<? extends RequestExecutor> executorClz, Class<?>... parameterTypes) {
  		this.requestExecutorType = executorClz;
  		return this;
  	}
	*/
	
	public abstract <T> ServerOptions option(ChannelOption<T> option, T value);
    
	public abstract <T> ServerOptions childOption(ChannelOption<T> childOption, T value);
	
}
