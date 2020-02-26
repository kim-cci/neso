package org.neso.core.request;

import org.neso.core.netty.ByteBasedWriter;

public interface Client extends Session {
 
	public boolean isConnected();
	
	public void disconnect();
	    
	public ByteBasedWriter getWriter();
}
