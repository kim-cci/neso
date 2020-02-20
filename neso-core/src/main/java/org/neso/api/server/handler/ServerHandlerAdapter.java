package org.neso.api.server.handler;

import java.lang.reflect.Method;

import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.HeadRequest;
import org.neso.core.request.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.StringUtil;

/**
 * AbstractServerHandler Adapter
 *
 * 
 * getApiIdFromHead 또는 getApiIdFromBody를 Override하여
 * head byte array나 body byte array로부터 API 식별값(String)을 반환해줘야 한다.
 */
public class ServerHandlerAdapter extends ServerHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected ServerHandlerAdapter(int headerLength) {
		super(headerLength);
		checkConcreteGetApi(this.getClass());
	}

    private void checkConcreteGetApi(Class<? extends ServerHandlerAdapter> c) {

        Class<?> clazz = c;
        boolean isConcreteGetApiFromHead = false;
        boolean isConcreteGetApiFromBody = false;
        boolean isConcreteGetBodyLength = false;
        while (!clazz.equals(ServerHandlerAdapter.class)) {
            Method[] thisMethods = clazz.getDeclaredMethods();
            for (Method method : thisMethods ) {
            	if ("getApiIdFromHead".equals(method.getName())) {
            		isConcreteGetApiFromHead = true;
            	}
            	
            	if ("getApiIdFromBody".equals(method.getName())) {
            		isConcreteGetApiFromBody = true;
            	}
            	
            	if ("getBodyLength".equals(method.getName())) {
            		isConcreteGetBodyLength = true;
            	}
            }
            clazz = clazz.getSuperclass();
        }
        
        if (!isConcreteGetApiFromHead && !isConcreteGetApiFromBody) {
        	logger.error("required override 'getApiIdFromHead' or 'getApiIdFromBody' in serverHandler ->  {} ", this.getClass().getSimpleName());
        	//throw new RuntimeException("required override 'getApiIdFromHead' or 'getApiIdFromBody' in ServerHandler");
        }
        
        if (!isConcreteGetBodyLength) {
        	logger.error("required override 'getBodyLength' in serverHandler ->  {} ", this.getClass().getSimpleName());
        }
    }

    
    @Override
    public int getBodyLength(HeadRequest request) {
    	return 0;
    }
    
	@Override
	protected String getApiIdFromHead(byte[] head) {
		return StringUtil.EMPTY_STRING;
	}

	@Override
	protected String getApiIdFromBody(byte[] body) {
		return StringUtil.EMPTY_STRING;
	}

	@Override
	public byte[] preApiExecute(Session session, HeadBodyRequest request) {
		return null;
	}

	@Override
	public byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response) {
		return null;
	}

	@Override
	protected byte[] exceptionCaughtRequestIO(Session session, Throwable exception) {
		return null;
	}

	@Override
	protected byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception) {
		return null;
	}
}