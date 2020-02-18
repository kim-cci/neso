package org.neso.sample.chapter4;


import org.neso.api.handler.server.ServerHandlerAdapter;
import org.neso.api.handler.server.listener.ListenerExceptionCaughtRequestExecute;
import org.neso.api.handler.server.support.HeadBasedServerHandler;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;
import org.neso.core.server.Server;

public class ExceptionHandleServerTest {

	public static void main(String[] args) {
		
		ServerHandlerAdapter serverHandler = new HeadBasedServerHandler(8, 0, 2, 2, 6);
		serverHandler.registApi("sign", request -> {
			
			if ("john".equals(new String(request.getBodyBytes()))) {
				throw new RuntimeException("중복 이름"); //강제로 예외발생
			}
			return "u001".getBytes(); //회원 번호 리턴
		});
		serverHandler.registApi("search", request -> {
			//회원 조회 처리
			return "john".getBytes(); //회원 이름 리턴
		});
		
		
		serverHandler.attachListenerExceptionCaughtApiExecute(new ListenerExceptionCaughtRequestExecute() {
			
			@Override
			public byte[] event(Session session, HeadBodyRequest request, Throwable exception) {
				System.out.println("예외가 발생 했습니다." + exception.toString());
				System.out.println("요청 ip " + session.getRemoteAddr());
				return "fail".getBytes();
			}
		});

		Server server = new Server(serverHandler, 10001);
		server.start();
	}
}
