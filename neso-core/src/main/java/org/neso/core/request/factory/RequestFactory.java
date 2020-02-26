package org.neso.core.request.factory;

import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.OperableHeadBodyRequest;

public interface RequestFactory {
	
	public boolean isRepeatableReceiveRequest();
	
	public OperableHeadBodyRequest newHeadBodyRequest(Client client, RequestHandler requestHandler);
}
