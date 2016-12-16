package com.acme.exception;

/**
 * Created by kobzev on 16.12.16.
 */
public class EmailConversionException extends RuntimeException {

	public EmailConversionException() {
		super();
	}

	public EmailConversionException(final String message) {
		super(message);
	}

	public EmailConversionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EmailConversionException(final Throwable cause) {
		super(cause);
	}
}
