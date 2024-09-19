package com.vcp.hessen.kurhessen.features.usermanagement.exceptions;

public class TribeNotExistsException extends RuntimeException {
    public TribeNotExistsException(String message) {
        super(message);
    }

    public TribeNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
