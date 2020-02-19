package org.neso.api.handler.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.neso.api.Api;
import org.neso.api.handler.ApiHandler;
import org.neso.api.handler.server.task.ApiRequestTask;
import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;
import org.neso.core.request.handler.AbstractRequestHandler;
import org.neso.core.request.handler.task.RequestTaskThreadPool;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerHandler extends AbstractRequestHandler implements ApiHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, Api> apiHandlerMap = new ConcurrentHashMap<String, Api>();

	protected ServerHandler(int headerLength) { 
		this(headerLength, DEFAULT_TASK_MAX_THREAD_CNT);
	}

	public ServerHandler(int headerLength, int maxThreads) {
		super(headerLength);
		setRequestTaskPool(new RequestTaskThreadPool(maxThreads));
	}

	@Override
	public void onConnect(Client client) {
		
	}
	
	@Override
	public void onDisConnect(Client client) {
		
	}

	@Override
	public Api apiMatch(HeadBodyRequest request) {
		
		String apiKey = getApiIdFromHead(request.getHeadBytes());
		if (StringUtils.isEmpty(apiKey)) {
			apiKey = getApiIdFromBody(request.getBodyBytes());
		}
		
		if (StringUtils.isEmpty(apiKey)) {
			return null;
		}

		for(Map.Entry<String, Api> api : apiHandlerMap.entrySet()) {
			if (StringUtils.equals(api.getKey().trim(), apiKey.trim())) {
				return api.getValue();
			}
		}
		return null;
    }
	

	@Override
	public void onRequest(Client client, HeadBodyRequest req) {
		
		if (req instanceof OperableHeadBodyRequest) {
			
			OperableHeadBodyRequest request = (OperableHeadBodyRequest) req;
			
			ApiRequestTask task = new ApiRequestTask(client, request, this, this);
			
			getRequestTaskPool().invoke(task, client, req);
			
		} else {
			throw new RuntimeException("not... request instanceof OperableHeadBodyRequest ");
		}
	}

	@Override
	public void registApi(String apiKey, Api api) {
		apiHandlerMap.put(apiKey, api);
        logger.debug("{}({}) Api added to [{}]", apiKey, api.getClass().getSimpleName(), this.getClass().getSimpleName());
	}

	@Override
	final public byte[] doRequest(Client client, HeadBodyRequest request) {
		//not used
		return null;
	}

	@Override
	final public void onExceptionRequestIO(Client client, Throwable exception) {
		
		byte[] errorMessage = "read/write error".getBytes();
		try {
			errorMessage = exceptionCaughtRequestIO(client, exception);
		} catch (Exception e) {
			logger.error("occurred serverHandler's exceptionCaughtRequestIO ");
		}
		
		client.write(errorMessage, true);
	}
	
	@Override
	final public void onExceptionRequestExecute(Client client, HeadBodyRequest request, Throwable exception) {
		byte[] errorMessage = "request execute error".getBytes();
		
		try {
			errorMessage = exceptionCaughtRequestExecute(client, request, exception);
		} catch (Exception e) {
			logger.error("occurred serverHandler's exceptionCaughtRequestIO ");
		}
		client.write(errorMessage);
	}
	
	protected abstract String getApiIdFromHead(byte[] head);
	
	protected abstract String getApiIdFromBody(byte[] body);
	
	protected abstract byte[] exceptionCaughtRequestIO(Session session, Throwable exception);
	
	protected abstract byte[] exceptionCaughtRequestExecute(Session session, HeadBodyRequest request, Throwable exception);
}
