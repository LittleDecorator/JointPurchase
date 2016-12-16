package com.acme.exception;

/**
 * Created by kobzev on 15.12.16.
 */
public class TemplateException extends Exception {

	public TemplateException() {
	}

	public TemplateException(final String message) {
		super(message);
	}

	public TemplateException(final Throwable cause) {
		super(cause);
	}

	public TemplateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TemplateException(final String message, final Throwable cause,
							 final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}