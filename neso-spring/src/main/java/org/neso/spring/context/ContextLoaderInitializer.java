package org.neso.spring.context;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class ContextLoaderInitializer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Set<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
	private final Set<String> handlerBasePackages = new LinkedHashSet<String>();
	
	private boolean applyMappingAnnotataion = true;
	
	
	public void registerClass(Class<?>... annotatedClasses) {
		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
		this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
	}
	
	public void setHandlerBasePackages(String...basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.handlerBasePackages.addAll(Arrays.asList(basePackages));
	}
	
	public void setApplyMappingAnnotataion(boolean applyMappingAnnotataion) {
		this.applyMappingAnnotataion = applyMappingAnnotataion;
	}
	
	
	@SuppressWarnings("resource")
	public void startUp() {
		logger.info("start.. server application context.....");
		
        logger.debug("annotation config class.. {}", annotatedClasses);
		if (applyMappingAnnotataion) {
			annotatedClasses.add(AutoHandlerBeanMappingConfig.class);
		}
		
		Class<?>[] classes = annotatedClasses.toArray(new Class[annotatedClasses.size()]);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(classes);
        
        if (handlerBasePackages.size() > 0) {
        	String[] basePackages = handlerBasePackages.toArray(new String[handlerBasePackages.size()]);
        	ctx.scan(basePackages);
        }
        
        ctx.registerShutdownHook();

        ctx.refresh();
		logger.info("completed loading ..server application context.....");
	}
	
	
}
