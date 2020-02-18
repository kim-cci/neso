package org.neso.spring.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.neso.core.request.core.Api;
import org.neso.core.request.core.HeadAndBodyServerHandler;
import org.neso.core.request.core.support.server.ConnectionlessServer;
import org.neso.core.request.core.support.server.handler.MultiApiServerHandler;
import org.neso.spring.annotation.ApiHandlerMapping;
import org.neso.spring.annotation.MultiApiServerHandlerMapping;
import org.neso.spring.annotation.MultiApiServerHandlerMapping.ServerRunOption;
import org.neso.spring.context.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AutoHandlerBeanMappingConfig implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(ContextLoaderInitializer.class);
    
	@Autowired private ConfigurableApplicationContext context;
	
	@Autowired(required = false) private List<HeadAndBodyServerHandler> serverHandlers = new ArrayList<HeadAndBodyServerHandler>();
	@Autowired(required = false) private List<Api> apiHandlers = new ArrayList<Api>();
	
	@Autowired ServerContext serverContext;
	
	@Bean
	public ServerContext serverContext() {
		return new ServerContext();
	}
	
	public ServerContext getServerContext() {
		return serverContext;
	}

	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init() {

		if (serverHandlers != null && serverHandlers.size() > 0) {
			configurationServerHandlers(serverHandlers);
		}
		
		if (apiHandlers.size() > 0) {
			configurationApiHandlers(apiHandlers);
		}
		serverContext.executeServer();
	}

	private void configurationServerHandlers(List<HeadAndBodyServerHandler> serverHandlers) {

		for (HeadAndBodyServerHandler serverHandler : serverHandlers) {
			MultiApiServerHandlerMapping serverHandlerMapping = serverHandler.getClass().getAnnotation(MultiApiServerHandlerMapping.class);
			if (serverHandlerMapping != null) {
            	if (StringUtils.isNotEmpty(serverHandlerMapping.serverHandlerName())) {
            		serverHandler.setServerHandlerName(serverHandlerMapping.serverHandlerName());
            	}
            	ServerRunOption[] options = serverHandlerMapping.runOptions();
				for (ServerRunOption runOption : options) {
					
					ConnectionlessServer server = new ConnectionlessServer(serverHandler, runOption.port());
					server.setReadTimeout(runOption.readTimeout());
					server.setMaxTreadCount(runOption.maxThreadCount());
					server.setRunMode(runOption.runMode());
					
					serverContext.addServerExecutor(server);
				}
				
				//logger.debug("{} server Handler, [ServerHandlerMapping] is defiend!!", serverHandler.getServerHandlerName());
			} else {
        		logger.debug("{} server Handler, [ServerHandlerMapping] is undefiend!!", serverHandler.getServerHandlerName());
			}
			serverContext.addServerHandler(serverHandler);
		}
	}
	
	
	private void configurationApiHandlers(List<Api> apiHandlers) {
		
        for (Api apiHandler : apiHandlers) {
        	
        	ApiHandlerMapping apiHandlerMapping = apiHandler.getClass().getAnnotation(ApiHandlerMapping.class);
            if (apiHandlerMapping != null) {

            	if (StringUtils.isNotEmpty(apiHandlerMapping.desc())) {
                	apiHandler.setHandlerDesc(apiHandlerMapping.desc());
            	}
            	
            	mappingApiHanderToServerHandler(apiHandler, apiHandlerMapping.id(), apiHandlerMapping.serverHandlerClz());
           		//logger.debug("{} api handler, [ApiHandlerMapping] is defiend!!", apiHandler.getHandlerDesc());
            } else {
        		logger.debug("{} api handler, [ApiHandlerMapping] is undefiend!!", apiHandler.getHandlerDesc());
            }
            serverContext.addApiHandler(apiHandler);
        }
	}
	
	private void mappingApiHanderToServerHandler(Api apiHandler, String apiId, Class<? extends MultiApiServerHandler>[] serverHanderClasses) {
        	
    	if ( apiId != null) {
    		if (serverHanderClasses != null && serverHanderClasses.length > 0) {
    			for (Class<? extends MultiApiServerHandler> serverHanderClass : serverHanderClasses) {
    				String[] beanNames = context.getBeanNamesForType(serverHanderClass);
    				if (beanNames != null && beanNames.length > 0) {
    					for (String beanName : beanNames) {
        					mappingToServerHander(beanName, apiId, apiHandler);
    					}
    				} else {
    					logger.debug("not fount '{}' server handler bean", serverHanderClass.getSimpleName());
    				}
    			}
    		} else {
    			logger.debug("can't mapping [{}-{}] apiHander, [serverHanderClasses] is Undefiend", apiId, apiHandler.getHandlerDesc());
    		}
    	} else {
    		logger.debug("can't mapping apiHander, [id] is Undefiend");
    	}
	}
	
	private void mappingToServerHander(String beanName, String apiId, Api apiHandler) {
		MultiApiServerHandler serverHander = context.getBean(beanName, MultiApiServerHandler.class);
		if (serverHander != null) {
			serverHander.addApiHandler(apiId, apiHandler);
		}
	}
	
}
