package org.neso.core.request.handler.task;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayRequestThreadExecutor extends AbstractRequestExecutor {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ThreadPoolExecutor tp;

	
	@Override
	public boolean isRunIoThread() {
		return false;
	}

	@Override
	public void init(int MaxExecuteSize) {
		super.init(MaxExecuteSize);
		
		this.tp = new ThreadPoolExecutor(getMaxRequets(),  getMaxRequets(), 
				2000l, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(getMaxRequets() / 2)
				, new DefaultThreadFactory(getClass(), true));
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
