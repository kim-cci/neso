package org.neso.bind.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtils {

	public static Class<?> getGeneric(Class<?> clz) throws ClassNotFoundException {
        return getGeneric(clz, 0);
	}
	
	public static Class<?> getGeneric(Class<?> clz, int index) throws ClassNotFoundException {
		ParameterizedType ge =  (ParameterizedType) clz.getGenericSuperclass();
        Type t = ge.getActualTypeArguments()[ index ];
        String className = t.toString().split(" ")[1]; //class .....  class 제거

        return Class.forName( className );
	}
	
	public static Class<?> getGeneric(Field fild) throws ClassNotFoundException {
		
		ParameterizedType ge =  (ParameterizedType) fild.getGenericType();
        Type t = ge.getActualTypeArguments()[ 0 ];
        String className = t.toString().split(" ")[1]; //class .....  class 제거

        return Class.forName( className );
	}
}
