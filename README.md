# neso
소켓 서버(-흔히 전문 통신) 개발 시 사용할 수 있는 고수준 프레임워크입니다.
손 쉽게 바이트-길이 기반으로 통신하는 서버를 만들 수 있습니다. 

## 특징
- 내부 구현은 netty를 사용하였습니다.
- 웹 개발자에게 생소하지 않은 추상화 레벨을 제공합니다.
- 대량 접속 및 대량 처리를 위한 인터페이스를 제공합니다.

## 환경
- 자바 1.6 이상

## quick start & example
- ServerHandler 생성
````java
//회원번호 조회 API
public class SearchApi implements Api {
	
	private Service service;
	
	@Override
	public byte[] handle(HeadBodyRequest request) throws Exception {
		
		//서비스 레이어 
		//,,,,
		return userNo.getBytes();
	}
}
````
````java
//회원 가입 API
public class SignUpApi implements Api {
	
	private Service service;
	
	@Override
	public byte[] handle(HeadBodyRequest request) throws Exception {
		
		byte[] bodyBytes = request.getBodyBytes();
		//서비스 레이어 
		//,,,,
		
		return "ok".getBytes(); //성공 응답
	}
}
````

- ServerHandler구현체와 Server로 서버 시작
````
public static void main(String[] args) {
		
	ServerHandler serverHandler = new HeadBasedServerHandler(8, 0, 2, 2, 6);
	serverHandler.registApi("search", new SignUpApi()); //회원번호 조회 API
	serverHandler.registApi("sign", new SearchApi()); //회원 가입 API
		
	new Server(serverHandler, 10002)
    .maxConnections(5000)
		.connectionOriented(true)
		.readTimeoutMillisOnRead(1000)
		.start();
}
````

## architecture 


## Links - 개인 블로그
 * [Web Site](https://jronin.tistory.com/93)

## repositoryㅡ
