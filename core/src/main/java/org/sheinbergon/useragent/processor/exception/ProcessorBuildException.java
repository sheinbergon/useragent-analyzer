package org.sheinbergon.useragent.processor.exception;

public class ProcessorBuildException extends RuntimeException {

    public ProcessorBuildException(String message) {
        super(message);
    }

    public ProcessorBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
