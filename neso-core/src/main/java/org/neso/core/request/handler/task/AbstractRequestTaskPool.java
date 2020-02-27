package org.neso.core.request.handler.task;

import org.neso.core.netty.ByteBasedWriter;
import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.RequestRejectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestTaskPool implements RequestTaskPool {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	private final static String DEFAULT_REJECT_MESSAGE = "server is too busy";

	
	
	@Override
	public void register(RequestTask task, Client client, HeadBodyRequest request) {
		if (!invoke(task)) {
			logger.debug("request cant registered in the request pool");
			
			byte[] rejectMessage = DEFAULT_REJECT_MESSAGE.getBytes();
			if (request.getRequestHandler() instanceof RequestRejectListener) {
				
				RequestRejectListener listener = (RequestRejectListener) request.getRequestHandler();
			
				try {
					rejectMessage = listener.onRequestReject(client.getServerContext(), getMaxThreads(), request);
				} catch (Exception e) {
					logger.error("occurred requestRejectListner's onRequestReject", e);
				}
			}
			
			ByteBasedWriter writer = client.getWriter();
			writer.write(rejectMessage);
			writer.close();
		} else {
			logger.debug("request is registered in the request pool");
		}
	}
	
	public abstract boolean invoke(Runnable task);
}
