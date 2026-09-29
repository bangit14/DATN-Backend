package com.backend.candidateservice.cv.exception;

public class CvExportException extends RuntimeException {
    public CvExportException(String message) {
        super(message);
    }

    public CvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
