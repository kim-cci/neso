package org.neso.bind.exception;

import org.neso.bind.exception.DataBindException;

@SuppressWarnings("serial")
public class EmptyDataBindException extends DataBindException {

	public EmptyDataBindException(String field) {
        super(field, "Not Allow Empty value");
	}
}
