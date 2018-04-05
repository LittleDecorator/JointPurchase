package com.acme.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.warn("spring jms custom error handling example");
        log.error(t.getCause().getMessage());
    }

}
