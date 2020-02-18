package org.neso.sample.spring.config;

import org.neso.core.request.core.HeadAndBodyServerHandler;
import org.neso.core.request.core.support.api.HealthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Component;


@Configuration
@Import({PropertiesConfig.class})
@ComponentScan(
	includeFilters = @Filter(type = FilterType.ANNOTATION, value = {Component.class})
)
public class ServerConfig implements InitializingBean {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired 
    @Qualifier("koreaBankServerHandler") 
    HeadAndBodyServerHandler serverHandler;

    public void afterPropertiesSet() throws Exception {
    	HealthChecker.registerApiToServer(serverHandler, "a001", "a002", "a003");
    	
    	serverHandler.addApiHandler("a005", new EchoApiHandler());
    }
}