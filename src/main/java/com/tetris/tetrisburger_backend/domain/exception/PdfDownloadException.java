package com.tetris.tetrisburger_backend.domain.exception;

public class PdfDownloadException extends RuntimeException {
    public PdfDownloadException(String message) {
        super(message);
    }

    public PdfDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

}
