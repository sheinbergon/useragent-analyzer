package org.sheinbergon.useragent.analyzer.processor.exception;

/**
 * @author Idan Sheinberg
 */
public class V8ResourceAllocationException extends RuntimeException {

    public V8ResourceAllocationException(String message) {
        super(message);
    }

    public V8ResourceAllocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
