package com.itecheasy.ph3.web.exception;

public class AppException extends Exception {

    private static final long serialVersionUID = -2299978645662041500L;
    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
