package org.neso.core.request.handler.task;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SynchronousRequestTaskThreadExecutor implements RequestTaskExecutor {

	private ThreadPoolExecutor tp;
	private final int max;
	
	public SynchronousRequestTaskThreadExecutor(int max) {
		if (max < 1) {
			throw new RuntimeException("max count more than zero");
		}
		
		int coreThreadSize = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
		if (coreThreadSize > max) {
			coreThreadSize = max;
		}
		this.tp = new ThreadPoolExecutor(coreThreadSize,  max, 2000l, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		this.max = max;
	}
	
	@Override
	public boolean isRunIoWorkThread() {
		return false;
	}
	
	@Override
	public int getMaxExecuteSize() {
		return this.max;
	}
	
	@Override
	public synchronized boolean registerTask(RequestTask task) {
		boolean isRegTask = false;

		if (tp.getMaximumPoolSize() > tp.getActiveCount()) {
			tp.submit(task);
			isRegTask = true;
		}

		return isRegTask;
	}
}
