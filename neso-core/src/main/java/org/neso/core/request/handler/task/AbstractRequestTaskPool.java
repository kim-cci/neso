package org.neso.core.request.handler.task;

import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.RequestRejectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestTaskPool implements RequestTaskPool {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	private final static String DEFAULT_REJECT_MESSAGE = "server is too busy";

	
	
	@Override
	public void invoke(Runnable task, Client client, HeadBodyRequest request) {
		if (!register(task)) {
			logger.debug("request cant registered in the request pool");
			
			byte[] rejectMessage = DEFAULT_REJECT_MESSAGE.getBytes();
			if (this instanceof RequestRejectListener) {
				
				RequestRejectListener listener = (RequestRejectListener) this;
			
				try {
					rejectMessage = listener.onRequestReject(client.getServerContext(), getMaxThreads(), request);
				} catch (Exception e) {
					logger.error("occurred requestRejectListner's onRequestReject", e);
				}
			}
			client.write(rejectMessage);
		} else {
			logger.debug("request is registered in the request pool");
		}
	}
	
	public abstract boolean register(Runnable task);
}
