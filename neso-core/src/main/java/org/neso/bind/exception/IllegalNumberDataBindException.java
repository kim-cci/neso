package org.neso.bind.exception;

import org.neso.bind.exception.DataBindException;

@SuppressWarnings("serial")
public class IllegalNumberDataBindException extends DataBindException {

	public IllegalNumberDataBindException(String field, byte[] valueBytes) {
		super(field, "illegal number value", valueBytes);
		
	}
}
