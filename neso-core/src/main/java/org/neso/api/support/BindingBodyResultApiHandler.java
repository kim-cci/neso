package org.neso.api.support;


import org.neso.api.Api;
import org.neso.bind.DataScope;
import org.neso.bind.util.ByteBindUtils;
import org.neso.bind.util.ClassUtils;
import org.neso.core.request.HeadBodyRequest;

public abstract class BindingBodyResultApiHandler<HEAD, BODY , RESULT> implements Api {

	@SuppressWarnings("unchecked")
	@Override
	final public byte[] handle(HeadBodyRequest request) throws Exception {
		
 
		Class<?> headClz = ClassUtils.getGeneric(getClass(), 0);
		HEAD headObject = (HEAD) ByteBindUtils.toObject(request.getHeadBytes(), headClz, request.getRequestHandler().getCharset(), DataScope.REQUEST);
		
		Class<?> bodyClz = ClassUtils.getGeneric(getClass(), 1);
		BODY bodyObject = (BODY) ByteBindUtils.toObject(request.getBodyBytes(), bodyClz, request.getRequestHandler().getCharset(), DataScope.REQUEST);
		RESULT apiResult = handle(request, headObject, bodyObject);
		
		byte[] response = resultToBytes(apiResult);
		return response;

	}
	
	
	abstract protected RESULT handle(HeadBodyRequest request, HEAD head, BODY input) throws Exception;
	
	abstract protected byte[] resultToBytes(RESULT result);
}