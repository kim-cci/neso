package org.neso.sample.chapter4;

import org.neso.api.server.handler.ServerHandler;
import org.neso.core.request.Client;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.HeadRequest;
import org.neso.core.request.Session;
import org.neso.core.server.ServerContext;

public class InheritanceServerHandler extends ServerHandler {

	protected InheritanceServerHandler(int headerLength) {
		super(headerLength);
	}

	@Override
	public int getBodyLength(HeadRequest request) {
		// TODO 헤더 수신 후, 읽어야할 본문 길이 리턴
		return 0;
	}

	@Override
	protected String getApiKeyFromHead(byte[] head) {
		// TODO head에서 api식별자 추출
		return null;
	}

	@Override
	protected String getApiKeyFromBody(byte[] body) {
		// TODO body에서 api식별자 추출
		return null;
	}

	@Override
	protected byte[] exceptionCaughtRequestIO(Session session, Throwable exception) {
		// TODO 데이터 수신 오류 발생 시 호출, 응답 리턴, null 리턴 시 기본 메세지 응답
		return null;
	}

	@Override
	public byte[] preApiExecute(Session session, HeadBodyRequest request) {
		// TODO API 실행 전 호출, 리턴값이 널이 아닌 경우 API실행하지 않고 리턴 값으로 응답 처리
		return null;
	}

	@Override
	protected byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception) {
		// TODO API 실행 중 오류 발생 시 호출, 응답 리턴, null 리턴 시 기본 메세지 응답
		return null;
	}
	
	@Override
	public byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response) {
		// TODO API 실행 후 호출, 리턴값이 널이 아닌 경우 api응답값이 아닌 리턴 값으로 응답 처리
		return null;
	}

	@Override
	public void init(ServerContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect(Client client) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(Client client) {
		// TODO Auto-generated method stub
		
	}
}
