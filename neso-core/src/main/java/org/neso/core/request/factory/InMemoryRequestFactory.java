package org.neso.core.request.factory;

import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.InMemoryHeadBodyRequest;
import org.neso.core.request.internal.OperableHeadBodyRequest;

public class InMemoryRequestFactory extends AbstractRequestFactory {
	
	public InMemoryRequestFactory(boolean repeatableReceiveRequest) {
		setRepeatableReceiveRequest(repeatableReceiveRequest);
	}
	
	@Override
	public OperableHeadBodyRequest newHeadBodyRequest(Client client, RequestHandler requestHandler) {
		return new InMemoryHeadBodyRequest(requestHandler, client);
	}	
}
