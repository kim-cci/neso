package org.neso.core.request;

public interface Client extends Session {
 
	public void disconnect();
	    
	public void write(byte[] msg);
	
	public void write(byte[] msg, final boolean closeAfterWrite);
}
