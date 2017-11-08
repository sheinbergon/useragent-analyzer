package org.sheinbergon.useragent.analyzer.processor.exception;

/**
 * @author Idan Sheinberg
 */
public class UserAgentIngestionException extends RuntimeException {

    public UserAgentIngestionException(String message) {
        super(message);
    }

    public UserAgentIngestionException(Throwable cause) {
        super(cause);
    }
}
