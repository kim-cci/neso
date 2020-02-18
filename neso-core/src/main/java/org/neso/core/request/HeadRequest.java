package org.neso.core.request;

import org.neso.core.request.handler.RequestHandler;

public interface HeadRequest {
	
	public Session getSession();

	public long getRequestTime();

	public byte[] getHeadBytes();
	
	
	public void addAttribute(String key, Object value);
	
	public boolean removeAttribute(String key);
	
	public <T> T getAttribute(String key);
	
	public RequestHandler getRequestHandler();
	
}
