package org.sheinbergon.useragent.analyzer.exception;

public class AnalyzerBuildException extends RuntimeException {

    public AnalyzerBuildException(String message) {
        super(message);
    }

    public AnalyzerBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
