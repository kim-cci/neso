package org.neso.api.handler;

import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.Session;

public interface ApiHandler {


    /**
     * request로부터 API를 매칭한다.
     * 
     * 호출순서 : 3, call by request task 스레드
     * @param HeadBodyRequest
     * @return 매핑 API
     */
    public Api apiMatch(HeadBodyRequest request);
    
    /**
     *  API 전처리기, 맵핑된 API 실행전 호출된다.
     * 
     * 호출순서 : 4, call by request task 스레드
     * @param HeadBodyRequest
     * @return 클라이언트에게 내려줄 응답 byte array, null이 아니라면, api를 실행하지 않고 바로 응답한다. 
     */
    public byte[] preApiExecute(Session session, HeadBodyRequest request);
    
    /**
     * API 후처리기, API 실행 후 호출된다. API 실행 중 예외가 발생하면 exceptionCaughtApiExecute 된다.
     * 
     * 호출순서 : 5, call by request task 스레드
     * @param HeadBodyRequest
     * @param response
     * @return 클라이언트에게 내려줄 응답 byte array
     */
    public byte[] postApiExecute(Session session, HeadBodyRequest request, byte[] response);
	
	/**
	 * 핸들러에 API 등록
	 * @param apiId
	 * @param api
	 */
	public void registApi(String apiId, Api api);
	
}
