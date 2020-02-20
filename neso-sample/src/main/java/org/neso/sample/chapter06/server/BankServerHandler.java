package org.neso.sample.chapter06.server;

import org.neso.api.server.handler.support.HeadBasedServerHandler;
import org.neso.core.request.HeadBodyRequest;


public class BankServerHandler extends HeadBasedServerHandler {
	
	public BankServerHandler() {
		/**
		 * {@link Head} Head 구성 참고
		 * 총 헤더길이 40, 본문길이 10~14, 전문구분 0 ~ 10
		 */
		super(40, 10, 4, 0, 10);
	}

	
	/**
	 * 오류 발생 시,
	 * 요청한 헤더는 그대로 내려주고 본문에 응답코드(4)에 오류코드값 + 오류메시지(100)를 내려준다.
	 */
	public byte[] handleException(HeadBodyRequest request, Throwable exception) {
		byte[] response = null;
		if (request.getAttribute("_HEAD_") != null) {
			//ErrorInfo errorInfo = getErrorInfo(exception);
			//response = ArrayUtils.addAll(request.getHeaderBytes(), ByteBindUtils.toBytes(errorInfo));
		} else {
			//헤더가 안들어온 상태다.
		}
		return response;
	}
}