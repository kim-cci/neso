package org.neso.core.request.handler.task;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler.Sharable;

/**
 * request 처리 스레드 풀
 *
 * 상시 스레드 수 -> 코어 * 2
 * 최대 스레드 수 -> maxThreadSize
 * 
 * 임시 스레드 idle 시간 : 2초
 * 대기큐 사이즈 : 0
 */
@Sharable
public class RequestTaskThreadPool extends AbstractRequestTaskPool {
	
	private ThreadPoolExecutor tp;
	private final int maxThreads;
	
	public RequestTaskThreadPool(int maxThreads) {
		int coreThreadSize = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
		if (coreThreadSize > maxThreads) {
			coreThreadSize = maxThreads;
		}
		this.tp = new ThreadPoolExecutor(coreThreadSize,  maxThreads, 2000l, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		this.maxThreads = maxThreads;
	}
	
	public int getMaxThreads() {
		return this.maxThreads;
	}
	
	@Override
	public boolean isAsyncResponse() {
		return true;
	}
	
	
	@Override
	public synchronized boolean register(Runnable task) {
 
		boolean isRegTask = false;

		if (tp.getMaximumPoolSize() > tp.getActiveCount()) {
			tp.submit(task);
			isRegTask = true;
		}

		return isRegTask;
	}
}
