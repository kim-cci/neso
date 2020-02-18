package org.neso.sample.chapter4;


import org.neso.api.handler.server.ServerHandlerAdapter;
import org.neso.api.handler.server.support.HeadBasedServerHandler;
import org.neso.core.server.Server;

public class PrePostServerTest {

	public static void main(String[] args) {
		
		ServerHandlerAdapter serverHandler = new HeadBasedServerHandler(8, 0, 2, 2, 6);
		serverHandler.registApi("sign", request -> {
			//회원 가입 처리
			return "u001".getBytes(); //회원 번호 리턴
		});
		serverHandler.registApi("search", request -> {
			//회원 조회 처리
			return "john".getBytes(); //회원 이름 리턴
		});
		
		
		//API 실행 전 처리
		serverHandler.attachListnerPreApiExecute((session, request) -> {
			System.out.println("API 요청 IP -> " + session.getRemoteAddr());
			System.out.println("API 요청 시간 -> " + request.getRequestTime());
			System.out.println("API 요청 헤더 -> " + new String(request.getHeadBytes()));
			System.out.println("API 요청 바디 -> " + new String(request.getBodyBytes()));
			
			return null; // byte array 응답 시, api 실행하지 않고 return값 응답
		});
		
		//API 실행 후 처리
		serverHandler.attachListnerPostApiExecute((session, request, responseBytes) -> {
			System.out.println("API 결과  IP -> " + session.getRemoteAddr());
			System.out.println("API 결과 -> " + new String(responseBytes));
			
			return null; // byte array 응답 시, responseBytes 대신 return값 응답
		});

		Server server = new Server(serverHandler, 10001);
		server.start();
	}
}
