package org.neso.sample.chapter3;

import java.util.Date;

import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;

/**
 * 회원 가입 API
 */
public class SignUpApi implements Api {

	@Override
	public byte[] handle(HeadBodyRequest request) throws Exception {
		
		byte[] bodyBytes = request.getBodyBytes();
		long signUpTime = request.getRequestTime();
		
		String userName = new String(bodyBytes);//body byte array는 가입자 이름 있음
		Date signUpDate = new Date(signUpTime);
		
		//회원 가입처리
		System.out.println(signUpDate + " " + userName + " 가입 성공");
		
		return "ok".getBytes(); //성공 응답
	}
}