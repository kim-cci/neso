package org.neso.core.request.factory;

import org.neso.core.request.Client;
import org.neso.core.request.handler.RequestHandler;
import org.neso.core.request.internal.InMemoryHeadBodyRequest;
import org.neso.core.request.internal.OperableHeadBodyRequest;

public class InMemoryRequestFactory implements RequestFactory {
	
	
	@Override
	public OperableHeadBodyRequest newHeadBodyRequest(Client client, RequestHandler requestHandler) {
		return new InMemoryHeadBodyRequest(requestHandler, client);
	}	
}
