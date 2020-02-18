package org.neso.core.request.handler.task;

import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;

public interface RequestTaskPool {
	
	public int getMaxThreads();
	
	public boolean isAsyncResponse();
	
	public void invoke(Runnable task, Client client, HeadBodyRequest request);
}
