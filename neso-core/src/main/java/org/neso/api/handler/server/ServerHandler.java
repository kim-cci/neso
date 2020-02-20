package org.neso.api.handler.server;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.neso.api.Api;
import org.neso.core.exception.ApiNotFoundException;
import org.neso.core.exception.ClientAbortException;
import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;
import org.neso.core.request.handler.AbstractRequestHandler;
import org.neso.core.request.handler.task.RequestTask;
import org.neso.core.request.handler.task.RequestTaskThreadPool;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String MATCH_API_ATTR_NAME = "_matched_api_obj";
	private Map<String, Api> apiHandlerMap = new ConcurrentHashMap<String, Api>();

	protected ServerHandler(int headerLength) { 
		this(headerLength, DEFAULT_TASK_MAX_THREAD_CNT);
	}

	public ServerHandler(int headerLength, int maxThreads) {
		super(headerLength);
		setRequestTaskPool(new RequestTaskThreadPool(maxThreads));
		setRepeatableRequest(false);
	}

	@Override
	public void onConnect(Client client) {
		
	}
	
	
	@Override
	public void onDisConnect(Client client) {
		
	}

 
	private Api apiMatch(HeadBodyRequest request) {
		
		String apiKey = getApiKey(request);
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
	
	protected String getApiKey(HeadBodyRequest request) {
		String apiKey = getApiIdFromHead(request.getHeadBytes());
		if (StringUtils.isEmpty(apiKey)) {
			apiKey = getApiIdFromBody(request.getBodyBytes());
		}
		
		return apiKey;
	}
	

	@Override
	final public void onRequest(Client client, HeadBodyRequest req) {
		
		if (req instanceof OperableHeadBodyRequest) {
			
			OperableHeadBodyRequest request = (OperableHeadBodyRequest) req;
			
			try {
				Api matchApi = apiMatch(request);
				if (matchApi == null) {
					throw new ApiNotFoundException(request, null);
				}
				
				request.addAttribute(MATCH_API_ATTR_NAME, matchApi);
			} catch (Exception e) {
				throw new ApiNotFoundException(request, e);
			}
			
			RequestTask task = new RequestTask(client, request, this);
			
			getRequestTaskPool().register(task, client, req);
			
		} else {
			throw new RuntimeException("not... request instanceof OperableHeadBodyRequest ");
		}
	}

	public void registApi(String apiKey, Api api) {
		apiHandlerMap.put(apiKey, api);
        logger.debug("{}({}) Api added to [{}]", apiKey, api.getClass().getSimpleName(), this.getClass().getSimpleName());
	}

	@Override
	final public void doRequest(Client client, HeadBodyRequest request) throws Exception {
		Api exeucteApi = request.getAttribute(MATCH_API_ATTR_NAME);
		if (exeucteApi == null) {
			throw new ApiNotFoundException(request, null);
		}
		
		logger.debug("request task start.. {}", new Date());
		
		long taskStartTime = System.nanoTime();

		byte[] response = preApiExecute(client, request);
			
		if (response == null) {
			response = exeucteApi.handle(request);

			byte[] postR = postApiExecute(client, request, response);
			if (postR != null) {
				response = postR;
			}
		}
			
		long elapsedTime = System.nanoTime() - taskStartTime;	//TODO //지연 리스터 처리
		elapsedTime = elapsedTime / 1000000;
			
		logger.debug("request task end.. elapsedTime-> {} ms", elapsedTime);
		
		try {
			client.write(response == null ? new byte[0] : response);
		} catch (ClientAbortException cae) {
			//정상 응답 write가 지연될 경우 예외 발생
			onExceptionRequestIO(client, cae);	
		}
	}

	@Override
	final public void onExceptionRequestIO(Client client, Throwable exception) {
		
		byte[] errorMessage = null;
		try {
			errorMessage = exceptionCaughtRequestIO(client, exception);

		} catch (Exception e) {
			logger.error("occurred serverHandler's exceptionCaughtRequestIO ");
		}
		if (errorMessage == null) {
			errorMessage = "read/write error".getBytes();
		}
		client.write(errorMessage, true);
	}
	
	@Override
	final public void onExceptionDoRequest(Client client, HeadBodyRequest request, Throwable exception) {
		
		
		byte[] errorMessage = null;
		try {
			errorMessage = exceptionCaughtDoRequest(client, request, exception);
		} catch (Exception e) {
			logger.error("occurred serverHandler's exceptionCaughtRequestIO ");
		}
		if (errorMessage == null) {
			 errorMessage = "server error".getBytes();
		}
		client.write(errorMessage);
	}
	
	
	protected abstract String getApiIdFromHead(byte[] head);
	
	protected abstract String getApiIdFromBody(byte[] body);
	
	protected abstract byte[] preApiExecute(Session session, HeadBodyRequest request);
    
	protected abstract byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response);
	
	protected abstract byte[] exceptionCaughtRequestIO(Session session, Throwable exception);
	
	protected abstract byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception);
}
