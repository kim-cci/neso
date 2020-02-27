package org.neso.api.server.handler;
 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.neso.api.Api;
import org.neso.core.exception.ApiNotFoundException;
import org.neso.core.exception.ClientAbortException;
import org.neso.core.netty.ByteBasedWriter;
import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;
import org.neso.core.request.factory.InMemoryRequestFactory;
import org.neso.core.request.handler.AbstractRequestHandler; 
import org.neso.core.request.handler.task.RequestTaskThreadPool; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerHandler extends AbstractRequestHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public final static String MATCH_API_ATTR_NAME = "_matched_api_obj";
	
	private Map<String, Api> apiHandlerMap = new ConcurrentHashMap<String, Api>();

	protected ServerHandler(int headerLength) { 
		this(headerLength, DEFAULT_TASK_MAX_THREAD_CNT);
	}

	public ServerHandler(int headerLength, int maxThreads) {
		super(headerLength);
		setRequestTaskPool(new RequestTaskThreadPool(maxThreads));
		setRequestFactory(new InMemoryRequestFactory(false));
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
		String apiKey = getApiKeyFromHead(request.getHeadBytes());
		if (StringUtils.isEmpty(apiKey)) {
			apiKey = getApiKeyFromBody(request.getBodyBytes());
		}
		
		return apiKey;
	}
	
	public void registApi(String apiKey, Api api) {
		apiHandlerMap.put(apiKey, api);
        logger.debug("{}({}) Api added to [{}]", apiKey, api.getClass().getSimpleName(), this.getClass().getSimpleName());
	}

	@Override
	final public void onRequest(Client client, HeadBodyRequest request) {
		try {
			Api matchApi = apiMatch(request);
			if (matchApi == null) {
				throw new ApiNotFoundException(request, null);
			}
			
			request.addAttribute(MATCH_API_ATTR_NAME, matchApi);
		} catch (Exception e) {
			throw new ApiNotFoundException(request, e);
		}
		
		super.onRequest(client, request);
	}
	
	@Override
	final public void doRequest(Client client, HeadBodyRequest request) throws Exception {
		
		if (client.isConnected()) {
			Api exeucteApi = request.getAttribute(MATCH_API_ATTR_NAME);
			if (exeucteApi == null) {
				throw new ApiNotFoundException(request, null);
			}
	  
			byte[] response = preApiExecute(client, request);
				
			if (response == null) {
				response = exeucteApi.handle(request);

				byte[] postR = postApiExecute(client, request, response);
				if (postR != null) {
					response = postR;
				}
			}
	 
			try {
				ByteBasedWriter writer = client.getWriter();
				writer.write(response == null ? new byte[0] : response);
				writer.close();
				
			} catch (Exception e) {
				logger.error("write exception...");
			}
		} else {
			onExceptionRequestIO(client, new ClientAbortException(client));
		}
	}

	@Override
	final public void onExceptionRequestIO(Client client, Throwable exception) {
		logger.debug("onExceptionRequestIO 예외 발생 connect =" + client.isConnected(), exception);
		if (client.isConnected()) {
			byte[] errorMessage = null;
			try {
				errorMessage = exceptionCaughtRequestIO(client, exception);

			} catch (Exception e) {
				logger.error("occurred serverHandler's exceptionCaughtRequestIO ");
			}
			if (errorMessage == null) {
				errorMessage = "read/write error".getBytes();
			}

			ByteBasedWriter writer = client.getWriter();
			writer.write(errorMessage);
			writer.close();
			
			client.disconnect();
		}
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
		
		ByteBasedWriter writer = client.getWriter();
		writer.write(errorMessage);
		writer.close();
	}
	
	
	protected abstract String getApiKeyFromHead(byte[] head);
	
	protected abstract String getApiKeyFromBody(byte[] body);
	
	protected abstract byte[] preApiExecute(Session session, HeadBodyRequest request);
    
	protected abstract byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response);
	
	protected abstract byte[] exceptionCaughtRequestIO(Session session, Throwable exception);
	
	protected abstract byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception);
}
