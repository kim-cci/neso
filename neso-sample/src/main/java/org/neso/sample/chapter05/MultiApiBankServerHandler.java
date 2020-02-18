package org.neso.sample.chapter05;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.bind.util.ByteBindUtils;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.support.server.handler.HeadBasedServerHandler;


public class MultiApiBankServerHandler extends HeadBasedServerHandler {
	
	public MultiApiBankServerHandler() {
		/**
		 * Head 구성
		 * 0 ~ 10 바이트 = 전문번호, 10 ~ 14 바이트는 바디길이, 14 ~ 22 바이트는 요청일자
		 * 
		 * Sample => 전문번호:Deposit, 바디길이(헤더를제외한길이): 100(가변적), 요청날짜:20170727
		 * Head Bytes = [D][e][p][s][i][t][][][][0][1][0][0][0][2][0][1][7][0][7][3][7] 
		 */
		super(22, 10, 4, 0, 10);
	}
	

	
	@Override
	public byte[] exceptionCaughtApiExecute(HeadBodyRequest request, Throwable exception) {
		byte[] response = null;
		if (request.getAttribute("_HEAD_") != null) {
			ErrorInfo errorInfo = getErrorInfo(exception);
			response = ArrayUtils.addAll(request.getHeadBytes(), ByteBindUtils.toBytes(errorInfo));
		} else {
			//헤더가 안들어온 상태다.
		} 
		return response;
	}
	
	
	private ErrorInfo getErrorInfo(Throwable exception) {
		//......
		return null;
	}
}