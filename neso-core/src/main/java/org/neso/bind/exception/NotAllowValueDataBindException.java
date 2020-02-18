package org.neso.bind.exception;

import java.util.Arrays;

import org.neso.bind.exception.DataBindException;

@SuppressWarnings("serial")
public class NotAllowValueDataBindException extends DataBindException {

	byte[] data;
	String[] allowChars = {};
	
	public NotAllowValueDataBindException(String field, byte[] valueBytes, String... allowChar) {
        super(field, "Not allow value, value=[" + new String(valueBytes) + "], allow value=" + Arrays.toString(allowChar) , valueBytes);
        this.allowChars = allowChar;
	}

	public byte[] getDataBytes() {
		return this.data;
	}
	
	public String[] allowChars() {
		return this.allowChars;
	}
}
