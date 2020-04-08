package org.neso.sample.chapterx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Temp {
	
	private final static Logger logger = LoggerFactory.getLogger(Temp.class);
	

	public static void main(String[] args) {
		logger.info("start.. server application context.....");
		
		Class<?>[] classes = null;

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(classes);
        
        String[] basePackages = null;
        ctx.scan(basePackages);

        ctx.registerShutdownHook();

        ctx.refresh();
		logger.info("completed loading ..server application context.....");
	}
}
