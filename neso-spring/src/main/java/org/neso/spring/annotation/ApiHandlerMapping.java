package org.neso.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.neso.core.request.core.support.server.handler.MultiApiServerHandler;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ApiHandlerMapping {

    String id();
    
    /**
     */
    Class<? extends MultiApiServerHandler>[] serverHandlerClz() default {};
    
    String desc() default "";
}
