package com.acme.exception;

/**
 * Exception if something wrong while manipulation with Settings
 */
public class SettingsException extends RuntimeException {
    public SettingsException(String message) {
        super(message);
    }
}
