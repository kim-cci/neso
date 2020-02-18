package org.neso.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.neso.bind.DataScope;
import org.neso.bind.util.ByteBindUtils;

/**
 * Byte배열을 Object(Class)에 매핑할 때 사용
 * {@link ByteBindUtils}
 *  
 * List구현체에 붙일 수 있다.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ListField {
	
    /**
     */
    int listSizeFieldLength();
    
    String desc();
 
    DataScope scope() default DataScope.BOTH;
}