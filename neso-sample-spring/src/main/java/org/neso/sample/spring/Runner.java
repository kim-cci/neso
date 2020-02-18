package org.neso.sample.spring;


import org.neso.sample.spring.config.ServerConfig;
import org.neso.spring.context.ContextLoaderInitializer;

public class Runner {

	public void start() throws Exception {
		ContextLoaderInitializer initializer = new ContextLoaderInitializer();
		initializer.registerClass(ServerConfig.class);
		initializer.setHandlerBasePackages("org.neso.sample.spring.server", "org.neso.sample.spring.api");
		initializer.startUp();
	}
	
	public static void main(String[] args) throws Exception {
		new Runner().start();
	}
}
