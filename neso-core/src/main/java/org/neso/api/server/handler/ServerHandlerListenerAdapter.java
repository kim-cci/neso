package org.neso.api.server.handler;

import org.neso.api.server.handler.listener.ListenerExceptionCaughtRequestExecute;
import org.neso.api.server.handler.listener.ListenerExceptionCaughtRequestIO;
import org.neso.api.server.handler.listener.ListenerPostApiExecute;
import org.neso.api.server.handler.listener.ListenerPreApiExecute;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;


public abstract class ServerHandlerListenerAdapter extends ServerHandlerAdapter {

	protected ServerHandlerListenerAdapter(int headerLength) {
		super(headerLength);
	}


	private ListenerExceptionCaughtRequestExecute listenerExceptionCaughtApiExecute;
	final public ServerHandlerListenerAdapter attachListenerExceptionCaughtApiExecute(ListenerExceptionCaughtRequestExecute l) {
		this.listenerExceptionCaughtApiExecute = l;
		return this;
	}
	
	@Override
	protected byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception) {
		if (listenerExceptionCaughtApiExecute == null) {
			return "server error".getBytes();
		} else {
			return listenerExceptionCaughtApiExecute.event(session, request, exception);
		}
	}
    
    
	private ListenerExceptionCaughtRequestIO listenerExceptionCaughtRequestIO;
	final public ServerHandlerListenerAdapter attachListenerExceptionCaughtRequestIO(ListenerExceptionCaughtRequestIO l) {
		this.listenerExceptionCaughtRequestIO = l;
		return this;
	}
	
	@Override
	protected byte[] exceptionCaughtRequestIO(Session session, Throwable exception) {
		if (listenerExceptionCaughtRequestIO == null) {
			return "read/write error".getBytes();
		} else {
			return listenerExceptionCaughtRequestIO.event(session, exception);
		}
	}

	
	private ListenerPreApiExecute listenerPreApiExecute;
	final public ServerHandlerListenerAdapter attachListnerPreApiExecute(ListenerPreApiExecute l) {
		this.listenerPreApiExecute = l;
		return this;
	}
	
	@Override
	public byte[] preApiExecute(Session session, HeadBodyRequest request) {
		if (listenerPreApiExecute != null) {
			return listenerPreApiExecute.event(session, request);
		} else {
			return null;
		}
	}

	private ListenerPostApiExecute listenerPostApiExecute;
	final public ServerHandlerListenerAdapter attachListnerPostApiExecute(ListenerPostApiExecute l) {
		this.listenerPostApiExecute = l;
		return this;
	}
	
	@Override
	public byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response) {
		if (listenerPostApiExecute != null) {
			return listenerPostApiExecute.event(session, request, response);
		} else {
			return null;
		}
	}
}