package org.neso.sample.chapter3;


import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;


/**
 * 회원 조회 API
 */
public class SearchApi implements Api {

	static class DB {
		static String getUserName(String userNo) { return null; }
	}
	
	@Override
	public byte[] handle(HeadBodyRequest request) throws Exception {
		
		byte[] bodyBytes = request.getBodyBytes();
		
		String userNo = new String(bodyBytes);//body byte array에 회원번호
		 
		String userName = DB.getUserName(userNo);  //회원 조회 처리
		return userName.getBytes();
	}
	
	
}