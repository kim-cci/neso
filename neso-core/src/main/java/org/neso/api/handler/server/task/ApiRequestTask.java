package org.neso.api.handler.server.task;

import java.util.Date;

import org.neso.api.Api;
import org.neso.api.handler.ApiHandler;
import org.neso.core.exception.ApiNotFoundException;
import org.neso.core.exception.ClientAbortException;
import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiRequestTask implements Runnable {
	 
	byte[] error_message = "request execute exception..".getBytes();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	final private OperableHeadBodyRequest request;
	final private RequestHandler requestHandler;
	final private ApiHandler apiHandler;
	final private Client client;
	
	public ApiRequestTask(Client client, OperableHeadBodyRequest request, RequestHandler requestHandler, ApiHandler apiHandler) {
		this.request = request; 
		this.requestHandler = requestHandler;
		this.client = client;
		this.apiHandler = apiHandler;
	}
		 
	public void run() {
		
		try {
			Api exeucteApi = null;
			try {
				exeucteApi = apiHandler.apiMatch(request);
			} catch (Exception e) {
				throw new ApiNotFoundException(request, e);
			}
			
			if (exeucteApi == null) {
				throw new ApiNotFoundException(request, null);
			}
			
			logger.debug("request task start.. {}", new Date());
			
			long taskStartTime = System.nanoTime();

			byte[] response = apiHandler.preApiExecute(client, request);
				
			if (response == null) {
				response = exeucteApi.handle(request);

				byte[] postR = apiHandler.postApiExecute(client, request, response);
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
				requestHandler.onExceptionRequestIO(client, cae);	
			}
 
		} catch (Exception e) {

			byte[] exceptionCaughtApiExecuteResponse = "server error-r".getBytes();
			try {
				requestHandler.onExceptionRequestExecute(client, request, e);	

			} catch (Exception e2) {
				//exceptionCaughtRequestExecute 에서 exception이 발생하면.. 접속 종료
				logger.error("occurred requestHandler's exceptionCaughtRequestExecute.. client disconnect", e2);
				client.write(exceptionCaughtApiExecuteResponse, true);	//TODO
			}
			client.write(exceptionCaughtApiExecuteResponse);
			
		} finally {
			request.release();
		}
	}
}
