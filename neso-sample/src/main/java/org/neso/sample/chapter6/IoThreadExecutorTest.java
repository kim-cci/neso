package org.neso.sample.chapter6;

import io.netty.handler.logging.LogLevel;

import org.neso.api.Api;
import org.neso.api.server.handler.support.SingleApiServerHandler;
import org.neso.core.request.HeadBodyRequest;
import org.neso.core.request.handler.task.IoThreadExecutor;
import org.neso.core.server.Server;

public class IoThreadExecutorTest {

	public static void main(String[] args) {
		
		new Server(new BodyEchoServerHandler(), 10000)
		.requestExecutorType(IoThreadExecutor.class)
		.maxRequests(100)
		.pipeLineLogLevel(LogLevel.DEBUG)
		.start();
	}
	
	
	
	
	
	
	
	
	
	
	
	
    static class BodyEchoServerHandler extends SingleApiServerHandler {

		public BodyEchoServerHandler() {
			super(2, new Api() {
				
				@Override
				public byte[] handle(HeadBodyRequest request) throws Exception {
					return request.getBodyBytes();
				}
			});
		}
	}
}
