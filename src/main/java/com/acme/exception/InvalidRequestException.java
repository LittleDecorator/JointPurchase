package com.acme.exception;

@SuppressWarnings("serial")
public class InvalidRequestException extends RuntimeException {

    private Object resource;

    public InvalidRequestException(String message, Object resource) {
        super(message);
        this.resource = resource;
    }

    public Object getResource() {
        return resource;
    }
}
