package org.neso.api.handler.server;

import java.util.Arrays;

import org.neso.api.handler.server.listener.ListenerExceptionCaughtRequestExecute;
import org.neso.api.handler.server.listener.ListenerExceptionCaughtRequestIO;
import org.neso.api.handler.server.listener.ListenerPostApiExecute;
import org.neso.api.handler.server.listener.ListenerPreApiExecute;
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

	
	public ServerHandlerAdapter(int headerLength) {
		super(headerLength);
	}

	@Override
	public int getBodyLength(HeadRequest request) {
		String bodyLength = new String(request.getHeadBytes());
		return Integer.parseInt(bodyLength);
	}
	
	@Override
	public String getApiIdFromHead(byte[] head) {
		return StringUtil.EMPTY_STRING;
	}

	@Override
	public String getApiIdFromBody(byte[] body) {
		return StringUtil.EMPTY_STRING;
	}

 
	
	
	private ListenerExceptionCaughtRequestExecute listenerExceptionCaughtApiExecute;
	final public ServerHandlerAdapter attachListenerExceptionCaughtApiExecute(ListenerExceptionCaughtRequestExecute l) {
		this.listenerExceptionCaughtApiExecute = l;
		return this;
	}
	
	@Override
	protected byte[] exceptionCaughtRequestExecute(Session session, HeadBodyRequest request, Throwable exception) {
		if (listenerExceptionCaughtApiExecute == null) {
			logger.debug("exceptionCaughtApiExecute occured !! request -> [{}]", Arrays.toString(request.getAllBytes()), exception);
			return "server error".getBytes();
		} else {
			return listenerExceptionCaughtApiExecute.event(session, request, exception);
		}
	}
    
    
	private ListenerExceptionCaughtRequestIO listenerExceptionCaughtRequestIO;
	final public ServerHandlerAdapter attachListenerExceptionCaughtRequestIO(ListenerExceptionCaughtRequestIO l) {
		this.listenerExceptionCaughtRequestIO = l;
		return this;
	}
	
	@Override
	protected byte[] exceptionCaughtRequestIO(Session session, Throwable exception) {
		if (listenerExceptionCaughtRequestIO == null) {
			logger.debug("exceptionCaughtRequestIO occured !! clinet ip -> [{}]", session.getRemoteAddr(), exception);
			return "read/write error".getBytes();
		} else {
			return listenerExceptionCaughtRequestIO.event(session, exception);
		}
	}

	
	private ListenerPreApiExecute listenerPreApiExecute;
	final public ServerHandlerAdapter attachListnerPreApiExecute(ListenerPreApiExecute l) {
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
	final public ServerHandlerAdapter attachListnerPostApiExecute(ListenerPostApiExecute l) {
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