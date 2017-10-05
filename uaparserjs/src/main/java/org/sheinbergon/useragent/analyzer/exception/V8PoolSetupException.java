package org.sheinbergon.useragent.analyzer.exception;

/**
 * @author Idan Sheinberg
 */
public class V8PoolSetupException extends RuntimeException {

    public V8PoolSetupException(String message) {
        super(message);
    }

    public V8PoolSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
