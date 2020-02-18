package org.neso.spring.context;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.neso.core.request.core.Api;
import org.neso.core.request.core.HeadAndBodyServerHandler;
import org.neso.core.request.core.support.server.ConnectionlessServer;

public class ServerContext {
	
	private List<Api> registedApiHandlerBeans = new LinkedList<Api>();
	
	private List<HeadAndBodyServerHandler> registedServerHandlerBeans = new LinkedList<HeadAndBodyServerHandler>();
    
	private Set<ConnectionlessServer> executedServerExecutors = new HashSet<ConnectionlessServer>();
	
	
	
	
	public void addApiHandler(Api apiHandler) {
		registedApiHandlerBeans.add(apiHandler);
	}
	
	public void addServerHandler(HeadAndBodyServerHandler serverHandler) {
		registedServerHandlerBeans.add(serverHandler);
	}
	
	public void addServerExecutor(ConnectionlessServer serverExecutor) {
		executedServerExecutors.add(serverExecutor);
	}
	
	
	public List<Api> getRegistedBeanApiHandlers() {
		return registedApiHandlerBeans;
	}

	public List<HeadAndBodyServerHandler> getRegistedBeanServerHandlers() {
		return registedServerHandlerBeans;
	}

	public Set<ConnectionlessServer> getExecutedServerExecutors() {
		return executedServerExecutors;
	}
	
	public void executeServer() {
		for (ConnectionlessServer server : getExecutedServerExecutors()) {
			server.start();
		}
	}
}
