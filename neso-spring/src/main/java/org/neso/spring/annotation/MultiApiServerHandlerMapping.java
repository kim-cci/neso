package org.neso.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.neso.core.request.core.support.server.ConnectionlessServer.RunMode;
import org.springframework.stereotype.Component;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MultiApiServerHandlerMapping {

	ServerRunOption[] runOptions() default {};
    
    String serverHandlerName() default "";
    
	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	public @interface ServerRunOption {
		
		int port();
		
		int readTimeout() default 10;
		
		int maxThreadCount() default 50;
		
		RunMode runMode() default RunMode.NEW_THREAD;
	}
}