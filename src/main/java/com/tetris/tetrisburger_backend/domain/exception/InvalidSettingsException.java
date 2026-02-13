
package com.tetris.tetrisburger_backend.domain.exception;

public class InvalidSettingsException extends RuntimeException {
    public InvalidSettingsException(String message) {
        super(message);
    }
}
