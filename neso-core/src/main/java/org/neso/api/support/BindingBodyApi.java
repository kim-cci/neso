package org.neso.api.support;


import org.neso.api.Api;
import org.neso.bind.DataScope;
import org.neso.bind.util.ByteBindUtils;
import org.neso.bind.util.ClassUtils;
import org.neso.core.request.HeadBodyRequest;

public abstract class BindingBodyApi<IN, OUT> implements Api {

	@SuppressWarnings("unchecked")
	@Override
	final public byte[] handle(HeadBodyRequest request) throws Exception {
	
		
		IN bodyObject = (IN) ByteBindUtils.toObject(request.getBodyBytes(), ClassUtils.getGeneric(getClass()), request.getSession().getServerContext().requestHandler().getCharset(), DataScope.REQUEST);
		OUT response = handle(request, bodyObject);
		
		return ByteBindUtils.toBytes(response);

	}
	
	
	abstract protected OUT handle(HeadBodyRequest request, IN input) throws Exception;
}