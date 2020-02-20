package org.neso.core.request.handler.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestTaskQueuePool extends AbstractRequestTaskPool {
	
	final private int maxThreads;
	final private BlockingQueue<Runnable> taskPool;
	
	public RequestTaskQueuePool(int maxThreads) {
		this.taskPool = new LinkedBlockingQueue<Runnable>(maxThreads);
		this.maxThreads = maxThreads;
	}
	
	public int getMaxThreads() {
		return this.maxThreads;
	}
	
	@Override
	public boolean isAsyncResponse() {
		return false;
	}
	
	@Override
	public synchronized boolean invoke(Runnable task) {
		
		boolean reg = taskPool.offer(task);
		if (reg) {
			task.run();
			taskPool.remove();
		}
		return reg;
	}
}
