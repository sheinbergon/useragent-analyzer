package org.sheinbergon.useragent.analyzer.exception;

public class UserAgentAnalyzerBuildException extends RuntimeException {

    public UserAgentAnalyzerBuildException(String message) {
        super(message);
    }

    public UserAgentAnalyzerBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
