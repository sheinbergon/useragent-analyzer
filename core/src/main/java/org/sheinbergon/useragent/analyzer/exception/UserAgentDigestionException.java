package org.sheinbergon.useragent.analyzer.exception;

/**
 * @author Idan Sheinberg
 */
public class UserAgentDigestionException extends RuntimeException {

    public UserAgentDigestionException(String message) {
        super(message);
    }

    public UserAgentDigestionException(Throwable cause) {
        super(cause);
    }
}
