package org.neso.core.request.handler.task;

import java.util.concurrent.atomic.AtomicInteger;

public class IoWorkThreadExecutor implements RequestTaskExecutor {
	
	final private int maxCount;
 
	private AtomicInteger currentCount = new AtomicInteger(0);
	
	public IoWorkThreadExecutor(int maxCount) {
		if (maxCount < 1) {
			throw new RuntimeException("max count more than zero");
		}
		this.maxCount = maxCount;
	}
	
	@Override
	public boolean isRunIoWorkThread() {
		return true;
	}
	
	@Override
	public int getMaxExecuteSize() {
		return this.maxCount;
	}
	
	@Override
	public boolean registerTask(RequestTask task) {
		
		if (currentCount.incrementAndGet() > maxCount) {
			currentCount.decrementAndGet();
			return false;
		}
		
		task.run();
		currentCount.decrementAndGet();
		return true;
	}
}
