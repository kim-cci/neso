package org.neso.sample.chapter5;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.neso.bind.util.ByteUtils;

public class ResponseUtils {
	
	/** 성공 응답 생성 시 호출 **/
	public static byte[] succes(byte[] bodyBytes, byte[] reqHeaderBytes) {
		return make("0000", bodyBytes, reqHeaderBytes);
	}
	
	/** 응답 생성 시 호출 **/
	public static byte[] make(String code, byte[] bodyBytes, byte[] reqHeaderBytes) {
		byte[] copyHeadBytes = Arrays.copyOf(reqHeaderBytes, reqHeaderBytes.length);
		byte[] responseHeadBytes = ByteUtils.insertAt(copyHeadBytes, 16, code.getBytes());
		
		int bodyByteLength = bodyBytes.length;
		String bodyLengh = String.format("%04d", bodyByteLength);
		responseHeadBytes = ByteUtils.insertAt(responseHeadBytes, 6, bodyLengh.getBytes());
		
		byte[] responseBytes = ArrayUtils.addAll(responseHeadBytes, bodyBytes);
		return responseBytes;
	}
}
