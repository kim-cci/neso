package org.neso.core.request.factory;

public abstract class AbstractRequestFactory implements RequestFactory {

	private boolean repeatableReceiveRequest = true;
	
	public void setRepeatableReceiveRequest(boolean repeatableReceiveRequest) {
		this.repeatableReceiveRequest = repeatableReceiveRequest;
	}
	
	@Override
	public boolean isRepeatableReceiveRequest() {
		return repeatableReceiveRequest;
	}

}
