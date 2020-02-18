package org.neso.core.request.handler;


import java.nio.charset.Charset;

import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.handler.task.RequestTask;
import org.neso.core.request.handler.task.RequestTaskPool;
import org.neso.core.request.handler.task.RequestTaskQueuePool;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.neso.core.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestHandler implements RequestHandler {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final static int DEFAULT_TASK_MAX_THREAD_CNT = 200;
	
	final private int headerLength;
	
	private Charset serverCharSet = Charset.defaultCharset();
	 
	
	private boolean repeatableRequest;

	private RequestTaskPool requestTaskPool;
	
	
	public AbstractRequestHandler(int headerLength) {
		this(headerLength, DEFAULT_TASK_MAX_THREAD_CNT);
	}
	
	public AbstractRequestHandler(int headerLength, int maxThreads) {
		this.headerLength = headerLength;
		this.requestTaskPool = new RequestTaskQueuePool(maxThreads);
	}
	
	public void setRequestTaskPool(RequestTaskPool requestTaskPool) {
		if (requestTaskPool == null) {
			throw new NullPointerException("requestTaskExecutorPool is not null");
		}
		this.requestTaskPool = requestTaskPool;
	}
	
	@Override
	public RequestTaskPool getRequestTaskPool() {
		return requestTaskPool;
	}
	
	@Override
	final public int getHeadLength() {
		return this.headerLength;
	}
	
	public void setCharset(Charset charSet) {
		this.serverCharSet = charSet;
	}
	
	@Override
	public Charset getCharset() {
		return serverCharSet;
	}
	
	@Override
	public void init(ServerContext context) {
		
	}

	@Override
	public void onRequest(Client client, HeadBodyRequest req) {
		
		if (req instanceof OperableHeadBodyRequest) {
			
			OperableHeadBodyRequest request = (OperableHeadBodyRequest) req;
			
			RequestTask task = new RequestTask(client, request, this);
			
			requestTaskPool.invoke(task, client, request);
			 
	
		} else {
			throw new RuntimeException("not... request instanceof OperableHeadBodyRequest ");
		}
	}

	
	@Override
	public void onExceptionRequestIO(Client client, Throwable exception) {
		
		byte[] errorMessage = "read/write error".getBytes();
		client.write(errorMessage, true);
	}
	
	@Override
	public void onExceptionRequestExecute(Client client, HeadBodyRequest request, Throwable exception) {
		byte[] errorMessage = "request execute error".getBytes();
		client.write(errorMessage, true);
		
	}
	
	
	public void setRepeatableRequest(boolean repeatableRequest) {
		this.repeatableRequest = repeatableRequest;
	}
	
	@Override
	public boolean isRepeatableRequest() {
		return repeatableRequest;
	}
	
	public abstract byte[] doRequest(Client client, HeadBodyRequest request);
}
