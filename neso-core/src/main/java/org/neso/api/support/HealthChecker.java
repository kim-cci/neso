package org.neso.api.support;


import org.apache.commons.lang3.StringUtils;
import org.neso.api.Api;
import org.neso.api.handler.server.ServerHandler;
import org.neso.core.request.HeadBodyRequest;


public class HealthChecker {

    private boolean serviceOn = true;

    protected HealthChecker(ServerHandler serverHandler, String healthCheckApiId, String serviceOnApiId, String serviceOffApiId) {
    	
    	final HealthChecker manager = this;
    	
    	if (serverHandler == null) {
    		throw new IllegalArgumentException("required serverHanlder");
    	}
    	if (StringUtils.isEmpty(healthCheckApiId)) {
    		throw new IllegalArgumentException("required healthCheckApiId");
    	}
    	if (StringUtils.isEmpty(serviceOnApiId)) {
    		throw new IllegalArgumentException("required serviceOnApiId");
    	}
    	if (StringUtils.isEmpty(serviceOffApiId)) {
    		throw new IllegalArgumentException("required serviceOffApiId");
    	}
 
    	serverHandler.registApi(healthCheckApiId, new Api() {
 
            @Override
            public byte[] handle(HeadBodyRequest request) throws Exception {
                return manager.isRun() ? "OK".getBytes() : "FALSE".getBytes();
            }
        });
    	
    	serverHandler.registApi(serviceOnApiId, new Api() {
            @Override
            public byte[] handle(HeadBodyRequest request) throws Exception {
            	manager.setRun(true);
                return "OK".getBytes();
            }
        });
    	
    	serverHandler.registApi(serviceOffApiId, new Api() {
            @Override
            public byte[] handle(HeadBodyRequest request) throws Exception {
            	manager.setRun(false);
                return "OK".getBytes();
            }
        });
    }
 
    protected boolean isRun() {
    	return serviceOn;
    }
    
    public void setRun(boolean run) {
    	this.serviceOn = run;
    }
    
    public static void registerHealthCheckApi(ServerHandler serverHandler, String healthCheckUrl, String serviceOnUrl, String serviceOffUrl) {
    	new HealthChecker(serverHandler, healthCheckUrl, serviceOnUrl, serviceOffUrl);
    }
}
