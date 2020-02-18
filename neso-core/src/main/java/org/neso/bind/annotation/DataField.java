package org.neso.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.util.ByteBindUtils;


/**
 * {@link ByteBindUtils}
 * 
 * DataField int은  최대 9자리, 
 * DataField long은 최대 18자리  
 * DataField boolean은 1자리 
 * DataField enum
 * DataField String
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataField {
    
    /**
     */
    int length();
    
    /**
     */
    String desc(); //설명은 필수로 하자
    
    /**
     */
    DataType attr() default DataType.TEXT;
    
    /**
     * default = true
     */
    boolean required() default true;
    
    
    DataScope scope() default DataScope.BOTH;
    
    String initReqValue() default "";
    
    
}