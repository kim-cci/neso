package org.neso.sample.chapter4;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.api.server.handler.ServerHandler;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.HeadRequest;
import org.neso.core.request.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 결제 서버
 */
public class PaymentServer extends ServerHandler {

	final Logger logSystem = LoggerFactory.getLogger(this.getClass());
	
	private static final int HEAD_LENGTH = 4;
	
	public PaymentServer() {
		super(HEAD_LENGTH);
	}
	
	@Override
	public int getBodyLength(HeadRequest request) {
        	//본문 길이는 헤더에 4바이트(integer )에 정의되어 있음
		return ByteBuffer.wrap(request.getHeadBytes()).getInt();	
	}
	
	@Override
	protected String getApiIdFromHead(byte[] head) {
		//API 식별자는 BODY에 있음
		return null;
	}

	@Override
	protected String getApiIdFromBody(byte[] body) {
		//API 식별자는 BODY의 0 ~ 9바이트에 있음, offset = 0, length = 10
		return new String(Arrays.copyOfRange(body, 0, 10), getCharset()).trim();
	}

	@Override
	public byte[] preApiExecute(Session session, HeadBodyRequest request) {
		//API 전처리 없음
		return null;
	}

	@Override
	public byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response) {
		logSystem.info("api result, ip={}, head={}", session.getRemoteAddr(), Arrays.toString(response));
		//요청 헤더 + API 실행 결과로 응답 내려주기
        	return ArrayUtils.addAll(request.getHeadBytes(), response);	
	}


	@Override
	protected byte[] exceptionCaughtRequestIO(Session session, Throwable exception) {
		return null; // i/o 예외는 기본 메세지 응답
	}

	@Override
	protected byte[] exceptionCaughtDoRequest(Session session, HeadBodyRequest request, Throwable exception) {
		byte[] headers = request.getHeadBytes();
		logSystem.error("API 실행 오류 발생, ip={}, head={}, error={}", session.getRemoteAddr(), Arrays.toString(headers), exception);
		//요청 헤더 + API 실행 결과로 응답 내려주기
        	return ArrayUtils.addAll(request.getHeadBytes(), "server error".getBytes());	
	}
}
