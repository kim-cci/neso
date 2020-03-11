package org.neso.core.server;

public class ServerOptions {
	
	final private boolean connectionOriented;
	
	final private int requestTaskExecutorPoolSize;
	
	final private int readTimeoutMillisOnRead;
	
	final private int writeTimeoutMillis;
	
	final private int maxConnections;
   
	final private int maxRequestBodyLength;
	
	final private boolean inoutLogging;

	
    public ServerOptions(boolean connectionOriented, int requestTaskExecutorPoolSize, int readTimeoutMillisOnRead, int writeTimeoutMillis, 
    		int maxConnections, int maxRequestBodyLength, boolean inoutLogging) {
    	
    	this.connectionOriented = connectionOriented;
    	this.requestTaskExecutorPoolSize = requestTaskExecutorPoolSize;
    	this.readTimeoutMillisOnRead = readTimeoutMillisOnRead;
    	this.writeTimeoutMillis = writeTimeoutMillis;
    	this.maxConnections = maxConnections;
    	this.maxRequestBodyLength = maxRequestBodyLength;
    	this.inoutLogging = inoutLogging;
	}


	public boolean isConnectionOriented() {
		return connectionOriented;
	}

	public int getRequestTaskExecutorPoolSize() {
		return requestTaskExecutorPoolSize;
	}

	public int getReadTimeoutMillisOnRead() {
		return readTimeoutMillisOnRead;
	}


	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
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
}