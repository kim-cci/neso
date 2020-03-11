package org.neso.core.request.handler.task;


public interface RequestTaskExecutor {
	
	public boolean isRunIoWorkThread();
	
	public int getMaxExecuteSize();
	
	public boolean registerTask(RequestTask task);
}
