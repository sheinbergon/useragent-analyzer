package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;

import java.util.Optional;

/**
 * @param <INGESTION> Analysis result object-graph root
 * @author Idan Sheinberg
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Analyzer<INGESTION> {

    protected abstract Optional<INGESTION> ingest(String rawUserAgent) throws UserAgentIngestionException;

    protected abstract Ingredients digest(INGESTION ingestion) throws UserAgentDigestionException;

    public Ingredients analyze(String rawUserAgent) throws UserAgentIngestionException, UserAgentDigestionException {
        return ingest(rawUserAgent)
                .map(this::digest)
                .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results"));
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder {
        public abstract Analyzer build();
    }


    @Override
    public void finalize() {
        this.teardown();
    }
}
