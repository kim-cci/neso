package org.neso.core.request.handler.task;

import java.util.Date;

import org.neso.core.request.Client;
import org.neso.core.request.handler.AbstractRequestHandler;
import org.neso.core.request.internal.OperableHeadBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestTask implements Runnable {
	 
	byte[] error_message = "request execute exception..".getBytes();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	final private OperableHeadBodyRequest request;
	final private AbstractRequestHandler requestHandler;
	final private Client client;
	
	public RequestTask(Client client, OperableHeadBodyRequest request, AbstractRequestHandler requestHandler) {
		this.request = request; 
		this.requestHandler = requestHandler;
		this.client = client;
	}
		 
	public void run() {
		logger.debug("request task start.. {}", new Date());
		
		long taskStartTime = System.nanoTime();
		
		byte[] response = null;
		try {
			response = requestHandler.doRequest(client, request);
			
			long elapsedTime = System.nanoTime() - taskStartTime;	//TODO //지연 리스터 처리
			elapsedTime = elapsedTime / 1000000;

			logger.debug("request task end.. elapsedTime-> {} ms", elapsedTime);
		} catch (Exception e) {
			
			try {
				requestHandler.onExceptionRequestExecute(client, request, e);
				
			} catch (Exception e2) {
				logger.error("occurred requestHandler's exceptionCaughtRequestExecute.. client disconnect", e2);
				client.disconnect();
			}
		}


		try {
			client.write(response == null ? new byte[0] : response);
			
		} catch (Exception cae) {
			//정상 응답 write가 지연될 경우 예외 발생
			requestHandler.onExceptionRequestIO(client, cae);	
		}

		request.release();
	}
}


