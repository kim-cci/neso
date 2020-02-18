package org.neso.api.support;

import org.neso.api.Api;
import org.neso.core.request.HeadBodyRequest;

public class FixedResponseApi implements Api {

	private byte[] fixed;
	
	public FixedResponseApi(String fixed) {
		this.fixed = fixed.getBytes();
	}
	
	public FixedResponseApi(byte[] fixed) {
		this.fixed = fixed;
	}
	
	@Override
	public byte[] handle(HeadBodyRequest request) throws Exception {
		return fixed;
	}
}
