package org.neso.core.request.handler;


import java.nio.charset.Charset;

import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.factory.AbstractRequestFactory;
import org.neso.core.request.factory.InMemoryRequestFactory;
import org.neso.core.request.factory.RequestFactory;
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
	
	protected final static boolean REPEATALBE_RECEIVE_REQUEST = true;
	
	final private int headerLength;
	
	private Charset serverCharSet = Charset.defaultCharset();

	private RequestTaskPool requestTaskPool;
	
	private AbstractRequestFactory requestFactory;
	
	public AbstractRequestHandler(int headerLength) {
		this(headerLength, DEFAULT_TASK_MAX_THREAD_CNT, REPEATALBE_RECEIVE_REQUEST);
	}
	
	public AbstractRequestHandler(int headerLength, int maxThreads, boolean repeatableReceiveRequest) {
		this.headerLength = headerLength;
		this.requestTaskPool = new RequestTaskQueuePool(maxThreads);
		this.requestFactory = new InMemoryRequestFactory(repeatableReceiveRequest);
	}
	
	public void setRequestFactory(AbstractRequestFactory requestFactory) {
		if (requestFactory == null) {
			throw new NullPointerException("requestFactory is not null");
		}
		this.requestFactory = requestFactory;
	}
	
	public void setRepeatableReceiveRequest(boolean repeatableReceiveRequest) {
		requestFactory.setRepeatableReceiveRequest(repeatableReceiveRequest);
	}
	
	public void setRequestTaskPool(RequestTaskPool requestTaskPool) {
		if (requestTaskPool == null) {
			throw new NullPointerException("requestTaskExecutorPool is not null");
		}
		this.requestTaskPool = requestTaskPool;
	}
	
	@Override
	public RequestFactory getRequestFactory() {
		return requestFactory;
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
			
			requestTaskPool.register(task, client, request);
			 
	
		} else {
			throw new RuntimeException("not... request instanceof OperableHeadBodyRequest ");
		}
	}
}
