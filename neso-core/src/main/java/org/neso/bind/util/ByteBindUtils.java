package org.neso.bind.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.neso.bind.DataScope;
import org.neso.bind.DataType;
import org.neso.bind.annotation.DataField;
import org.neso.bind.annotation.ListField;
import org.neso.bind.annotation.ObjectField;
import org.neso.bind.exception.NotAllowValueDataBindException;
import org.neso.bind.util.ByteUtils;
import org.neso.bind.util.ClassUtils;
import org.neso.bind.exception.DataBindException;
import org.neso.bind.exception.EmptyDataBindException;
import org.neso.bind.exception.IllegalNumberDataBindException;

public class ByteBindUtils {

    public final static Charset DEFAULT_CHARSET = Charset.forName("EUC-KR");
	public final static int OBJECT_LENGTH_UNCHECK = -1;
	
    public static <T> T toObject(byte[] in, Class<T> clz) {
        try {
			return toObject(in, clz.newInstance());
		} catch (InstantiationException e) {
			throw new DataBindException(e);
		} catch (IllegalAccessException e) {
			throw new DataBindException(e);
		}
    }
    
    public static <T> T toObject(byte[] in, T t) {
    	Param p = new Param(in, DEFAULT_CHARSET, DataScope.REQUEST);
        return toObject(t, p, "");
    }
    


    
    public static <T> T toObject(byte[] in, Class<T> clz, Charset charset, DataScope targetScope) {
        try {
			return toObject(in, clz.newInstance(), charset, targetScope);
		} catch (InstantiationException e) {
			throw new DataBindException(e);
		} catch (IllegalAccessException e) {
			throw new DataBindException(e);
		}
    }
    
    public static <T> T toObject(byte[] in, T t, Charset charset, DataScope targetScope) {
    	Param p = new Param(in, charset, targetScope);
        return toObject(t, p, "");
    }

    private static <T> T toObject(T t, Param p, String parentObjName) {
   
    	Charset charSet = p.charSet;
    	DataScope targetScope = p.targetScope;
    	
      	try {
    		
	        for (Field f : t.getClass().getDeclaredFields()) {
	            Class<?> dataFieldClz = f.getType();
	            f.setAccessible(true);

	            if (f.getAnnotation(DataField.class) != null) {
		            DataField dataField = f.getAnnotation(DataField.class);

	            	if (targetScope == DataScope.REQUEST && dataField.scope() == DataScope.RESPONSE ||
	            		targetScope == DataScope.RESPONSE && dataField.scope() == DataScope.REQUEST) {
			            continue;
	            	}
	            	
	            	String fieldDesc = getDesc(dataField.desc(), parentObjName);

	                byte[] sub = getSubArrayAndCheck(fieldDesc, p, dataField);

	                if (dataFieldClz == String.class) {
	                    f.set(t, toStringField(fieldDesc, dataField, sub, charSet, f.get(t)));
	                    
	                } else if (dataFieldClz == int.class) {
	                	f.set(t, toIntField(fieldDesc, dataField, sub, f.getInt(t)));

	                } else if (dataFieldClz == long.class) {
	                	f.set(t, tolongField(fieldDesc, dataField, sub, f.getLong(t)));

	                } else if (dataFieldClz == boolean.class) {
	                	f.set(t, toBooleanField(fieldDesc, dataField, sub, f.getBoolean(t)));
	                
	                } else if (dataFieldClz.isEnum()) {
	                	f.set(t, toEnumField(fieldDesc, dataField, sub, dataFieldClz.getEnumConstants(), f.get(t)));
	                	
	                } else {
	                    throw new DataBindException(fieldDesc, dataFieldClz.getSimpleName() + "은 허용되지 않는 자료형입니다.");
	                }
	            }
	            
	            if (f.getAnnotation(ObjectField.class) != null) {
		            ObjectField objField = f.getAnnotation(ObjectField.class);
	            	if (targetScope == DataScope.REQUEST && objField.scope() == DataScope.RESPONSE ||
	            		targetScope == DataScope.RESPONSE && objField.scope() == DataScope.REQUEST) {
			            continue;
	            	}

	            	if (f.getType().isInterface() || Modifier.isAbstract(f.getType().getModifiers())) {
	            		throw new DataBindException(getDesc(objField.desc(), parentObjName), "ObjectField는 interface, Abstract class 불가");
	            	}

					try {
						Object obj = toObject(f.getType().newInstance(), p, getDesc(objField.desc(), parentObjName));
	                	f.set(t, obj);
					} catch (InstantiationException e) {
						throw new DataBindException(f.getType().getClass().getName(), "Object new Instance()", e);	  
					}
	            }
	            
	            if (f.getAnnotation(ListField.class) != null) {
		            ListField listField = f.getAnnotation(ListField.class);

	            	if (targetScope == DataScope.REQUEST && listField.scope() == DataScope.RESPONSE ||
			            targetScope == DataScope.RESPONSE && listField.scope() == DataScope.REQUEST) {
			            continue;
	            	}
		            
	            	if (!org.apache.commons.lang3.ClassUtils.isAssignable(f.getType(), List.class)) {
	            		throw new DataBindException(listField.desc(), "ListField는 List자료형만 허용합니다.");
	            	}

	            	Class<?> listObjClass = null;
	            	try { 
	            		listObjClass = ClassUtils.getGeneric(f);
	            	} catch (ClassCastException cce) {
	            		throw new DataBindException(getDesc(listField.desc(), parentObjName), "ListField는 List<자료형> 제네릭 자료형이 필요합니다");
	            	}

	            	if (listObjClass.isInterface() || Modifier.isAbstract(listObjClass.getModifiers())) {
	            		throw new DataBindException(getDesc(listField.desc(), parentObjName), "ListField는 List<자료형>, 제네릭 자료형은 interface, Abstract 불가");
	            	}
	                
	            	byte[] sub = ArrayUtils.subarray(p.inBytes, 0, listField.listSizeFieldLength()); //귀찮.. 길이체크
	            	int count = Integer.parseInt(new String(sub));
	            	p.inBytes = ArrayUtils.subarray(p.inBytes, listField.listSizeFieldLength(), p.inBytes.length);

	            	List<Object> objList = new ArrayList<Object>();
	            	for (int i = 0; i < count; i++) {
						try {
							Object listObj = toObject(listObjClass.newInstance(), p, getDesc(listField.desc(), parentObjName));
		                	objList.add(listObj);
						} catch (InstantiationException e) {
							throw new DataBindException(listObjClass.getClass().getName(), "List new Instance()", e);	  
						}
	            	}
	        		f.set(t, objList);
	            }
	        }
	        
	        return t;
	        
    	}catch(IllegalAccessException iae) {
    		throw new DataBindException(iae);
    	}catch(ClassNotFoundException cne) {
    		throw new DataBindException(cne);
    	}
    }

    private static String getDesc(String desc, String parentObjName) {
        return StringUtils.isNotEmpty(parentObjName) ? parentObjName + "." + desc : desc ; 
    }

	private static byte[] getSubArrayAndCheck(String desc, Param p, DataField dataField) {

		if (p.inBytes.length < dataField.length()) {
	        throw new DataBindException(desc, "not enough length, required length = " + dataField.length() + ",actual length = " + p.inBytes.length, p.inBytes);
	  	}

        if (dataField.initReqValue().length() > 0) {
        	if (dataField.length() < dataField.initReqValue().getBytes(p.charSet).length) {
        		 throw new DataBindException(desc, "initReqValue 가 field의 length보다 큽니다.");
        	}
            p.inBytes = ArrayUtils.subarray(p.inBytes, dataField.length(), p.inBytes.length);
        	return ByteUtils.toBytes(dataField.initReqValue(), dataField.length(), p.charSet, dataField.attr());
        }
    
        byte[] retBytes = ArrayUtils.subarray(p.inBytes, 0, dataField.length());
        p.inBytes = ArrayUtils.subarray(p.inBytes, dataField.length(), p.inBytes.length);
		return retBytes;
	}

    private static String toStringField(String desc, DataField dataField, byte[] b, Charset charSet, Object defaultValue) {
        String str = new String(b, charSet);
        if (dataField.attr() == DataType.NUMERIC) {
        	throw new DataBindException(desc, "'String' field only [ALPHA_NUMERIC] or [TEXT] DataType");
        }
        if (dataField.attr() == DataType.TEXT) {
            str = str.trim();
        }
    	if (StringUtils.isEmpty(str)) {
    		
    		if (dataField.scope() != DataScope.RESPONSE && dataField.required()) { //임시로 RESPONSE 구분 넣음
        		throw new EmptyDataBindException(desc);
    		} else {
    			return defaultValue == null ? null: (String) defaultValue;
    		}
    	} else {
    		return str;
    	}
    }
    
    private static int toIntField(String desc,DataField dataField, byte[] sub, int defaultValue) {
    	//최대 자릿수 넘어가면 오류를 떨어뜨릴까? 인트는 9자리 넘어가면
        if (dataField.attr() != DataType.NUMERIC) {
        	throw new DataBindException(desc, "'int' field only [NUMERIC] DataType");
        }
    	String intStr = new String(sub);
        if (StringUtils.isEmpty(intStr.trim())) {
    		if (dataField.scope() != DataScope.RESPONSE && dataField.required()) { //임시로 RESPONSE 구분 넣음
            	throw new IllegalNumberDataBindException(desc, sub);
        	} else {
        		return defaultValue;
        	}
        } else {
        	try {
        		return Integer.parseInt(intStr);
        	} catch (NumberFormatException nfe) {
			      throw new IllegalNumberDataBindException(desc, sub);
			}
        }
    }
    
    private static long tolongField(String desc, DataField dataField, byte[] sub, long defaultValue) {
    	//최대 자릿수 넘어가면 오류를 떨어뜨릴까? LONG은 몇자리까지?
        if (dataField.attr() != DataType.NUMERIC) {
        	throw new DataBindException(desc, "'long' field only [NUMERIC] DataType");
        }
    	String longStr = new String(sub);
        if (StringUtils.isEmpty(longStr.trim())) {
    		if (dataField.scope() != DataScope.RESPONSE && dataField.required()) { //임시로 RESPONSE 구분 넣음
            	throw new IllegalNumberDataBindException(desc, sub);
        	} else {
        		return defaultValue;
        	}
        } else {
        	try {
        		return Long.parseLong(longStr);
        	} catch (NumberFormatException nfe) {
			      throw new IllegalNumberDataBindException(desc, sub);
			}
        }
     }
    
    private static boolean toBooleanField(String desc, DataField dataField, byte[] sub, boolean defaultValue) {
        if (dataField.attr() == DataType.NUMERIC) {
        	throw new DataBindException(desc, "'boolean' field only [ALPHA_NUMERIC] or [TEXT] DataType");
        }
    	String str = new String(sub);
        if (dataField.attr() == DataType.TEXT) {
            str = str.trim();
        }
        if (StringUtils.isEmpty(str.trim())) {
        	if (dataField.scope() != DataScope.RESPONSE && dataField.required()) { //임시로 RESPONSE 구분 넣음
        		throw new EmptyDataBindException(desc);
            }
            return defaultValue;

         } else {
        	if (StringUtils.equalsIgnoreCase(str, "y")) {
        		 return true;
        	}
        	if (StringUtils.equalsIgnoreCase(str, "n")) {
        		return false;
        	}
        	throw new NotAllowValueDataBindException(desc, sub, "Y", "N");
         }
    }

    private static Object toEnumField(String desc , DataField dataField, byte[] sub, Object[] enums, Object defaultValue) {
        if (dataField.attr() == DataType.NUMERIC) {
        	throw new DataBindException(desc, "'enum' field only [ALPHA_NUMERIC] or [TEXT] DataType");
        }
    	String enumStr = new String(sub);
        if (dataField.attr() == DataType.TEXT) {
        	enumStr = enumStr.trim();
        }
        if (StringUtils.isEmpty(enumStr.trim())) {
    		if (dataField.scope() != DataScope.RESPONSE && dataField.required()) { //임시로 RESPONSE 구분 넣음
                 throw new EmptyDataBindException(desc); 
             } else {
            	 return defaultValue == null ? null : defaultValue;
             }
        } else {
        	 String[] t = new String[enums.length];
        	 for (int i = 0; i < enums.length; i++) {
        		 if (StringUtils.equalsIgnoreCase(enums[i].toString(), enumStr)) {
        			 return enums[i];
        		 }
        		 t[i] = enums[i].toString();
        	 }
        	 throw new NotAllowValueDataBindException(desc, sub, t);
        }
    }
    
    public static byte[] toBytes(Object obj, DataScope targetScope) {
    	try {
    		Field[] fields =  obj.getClass().getDeclaredFields();

	        byte[] retBytes = {};

	        for (Field f : fields) {
	        	 f.setAccessible(true);
	        	 
	            if (f.getAnnotation(DataField.class) != null) {
	            	DataField dataField = f.getAnnotation(DataField.class);
	            	if (targetScope == DataScope.REQUEST && dataField.scope() == DataScope.RESPONSE ||
	            		targetScope == DataScope.RESPONSE && dataField.scope() == DataScope.REQUEST) {
			            continue;
	            	}

	                DataType type = dataField.attr();
	                Class<?> dataFieldClz = f.getType();
	                
	                if (dataFieldClz == String.class) {

	                    String src = (String) f.get(obj);
	                    byte[] tmp = ByteUtils.toBytes(src, dataField.length(), type);
	                    retBytes = ArrayUtils.addAll(retBytes, tmp);
	                    
	                } else if (dataFieldClz == int.class) {
	                    int iSrc = f.getInt(obj);
	                    byte[] tmp = ByteUtils.toBytes(String.valueOf(iSrc), dataField.length(), DataType.NUMERIC);
	                    retBytes = ArrayUtils.addAll(retBytes, tmp);
	                    
	                } else if (dataFieldClz == long.class) {
	                	long lSrc = f.getLong(obj);
	                    byte[] tmp = ByteUtils.toBytes(String.valueOf(lSrc), dataField.length(), DataType.NUMERIC);
	                    retBytes = ArrayUtils.addAll(retBytes, tmp);
	                    
	                } else if (dataFieldClz == boolean.class) {
	                	boolean bSrc = f.getBoolean(obj);
	                	String src = bSrc ? "Y" : "N";
	                    byte[] tmp = ByteUtils.toBytes(src, dataField.length(), dataField.attr());
	                    retBytes = ArrayUtils.addAll(retBytes, tmp);
	                    
	                } else if (dataFieldClz.isEnum()) {
	                	
	                	Object[] enums = dataFieldClz.getEnumConstants();
	                	Object enumStr =  f.get(obj);
	                	
	                	String[] allowEnumCodes = new String[enums.length];
	                	String find = null;
	                	for (int i = 0; i < enums.length; i++) {
	                		if (StringUtils.equalsIgnoreCase(enums[i].toString(), enumStr.toString())) {
	                			find = enums[i].toString();
	                		}
	                		allowEnumCodes[i] = enums[i].toString();
	                	}
	                	
	                	if (find != null) {
		                    byte[] tmp = ByteUtils.toBytes(find, dataField.length(), dataField.attr());
		                    retBytes = ArrayUtils.addAll(retBytes, tmp);
	                	} else {
	                		throw new DataBindException(dataField.desc(), enumStr + "은 허용되지 않는 enum코드입니다." + Arrays.toString(allowEnumCodes));
	                	}
	                  
	                } else {
	                    throw new DataBindException(dataField.desc(), dataFieldClz.getName() + "은 허용되지 않는 자료형입니다.");
	                }
	            }

	            if (f.getAnnotation(ListField.class) != null) {
	                ListField listFild = f.getAnnotation(ListField.class);

	            	if (targetScope == DataScope.REQUEST && listFild.scope() == DataScope.RESPONSE ||
	            		targetScope == DataScope.RESPONSE && listFild.scope() == DataScope.REQUEST) {
			            continue;
	            	}
	            	
	                List<?> list = (List<?>) f.get(obj);
	                byte[] tmp = ByteUtils.toBytes(String.valueOf(list.size()), listFild.listSizeFieldLength(), DataType.NUMERIC);
	                retBytes = ArrayUtils.addAll(retBytes, tmp);

	                for (int i = 0; i < list.size(); i++) {
	                	byte[] objTmp = toBytes(list.get(i), targetScope);
	                	retBytes = ArrayUtils.addAll(retBytes, objTmp);
	                }
	            }
	            
	            if (f.getAnnotation(ObjectField.class) != null) {
	            	ObjectField objField = f.getAnnotation(ObjectField.class);
	            	
	            	if (targetScope == DataScope.REQUEST && objField.scope() == DataScope.RESPONSE ||
	            		targetScope == DataScope.RESPONSE && objField.scope() == DataScope.REQUEST) {
			            continue;
	            	}
		            
                	byte[] objTmp = toBytes(f.get(obj), targetScope);
                	retBytes = ArrayUtils.addAll(retBytes, objTmp);
	            }
	        }
	        return retBytes;
	        
    	} catch(IllegalArgumentException iae) {
    		throw new DataBindingException(iae);
    		
    	} catch (IllegalAccessException ie) {
    		throw new DataBindingException(ie);
    		
		}
    }

    public static byte[] toBytes(Object obj) {
    	return toBytes(obj, DataScope.RESPONSE);
    }
    
    public static Map<String, Object> getNameValueMapAsField(Object obj)  throws IllegalAccessException, InstantiationException {
        Map<String, Object> retMap = new HashMap<String, Object>();
        
        Field[] fields =  obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            
        	org.neso.bind.annotation.DataField fild = f.getAnnotation(org.neso.bind.annotation.DataField.class);
            if (fild != null) {
                
                f.setAccessible(true); 
                
                String fieldName = f.getName();
                Object fieldValue =  f.get(obj);
                
                retMap.put(fieldName, fieldValue);
            }
        }
        return retMap;
    }
    
    
    
    public static class Param {
    	
    	public Param(byte[] inBytes, Charset charSet, DataScope targetScope) {
    		this.inBytes = inBytes;
    		this.charSet = charSet;
    		this.targetScope = targetScope;
		}
    	    	
    	byte[] inBytes;
    	Charset charSet;
    	DataScope targetScope;
    }
}
