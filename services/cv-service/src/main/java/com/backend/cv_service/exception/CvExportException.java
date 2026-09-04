package com.backend.cv_service.exception;

public class CvExportException extends RuntimeException {
    public CvExportException(String message) {
        super(message);
    }

    public CvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
