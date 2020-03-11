package org.neso.core.request.handler.task;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SynchronousRequestTaskThreadExecutor implements RequestTaskExecutor {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
		this.tp = new ThreadPoolExecutor(coreThreadSize,  max, 
				2000l, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>()
				, new DefaultThreadFactory(getClass(), true));
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
	public  boolean registerTask(RequestTask task) {
		try {
			tp.submit(task);
			return true;
		} catch (RejectedExecutionException ree) {
			return false;
		}
	}
	
	@Override
	public void shutdown() {
		//TODO 검증
		logger.info("SynchronousRequestTaskThreadExecutor (current task count={})", tp.getTaskCount());
		tp.shutdown();
		try {
			if (!tp.isShutdown() && !tp.awaitTermination(5, TimeUnit.SECONDS)) {

				if (!tp.isShutdown() && !tp.awaitTermination(5, TimeUnit.SECONDS)) {
					logger.info("shutdown fail...");
				}
			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
}
