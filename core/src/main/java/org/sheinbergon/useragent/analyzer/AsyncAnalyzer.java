package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @param <INGESTION> Analysis result object-graph root
 * @author Idan Sheinberg
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AsyncAnalyzer<INGESTION> {

    protected abstract CompletionStage<Optional<INGESTION>> ingest(String rawUserAgent) throws UserAgentIngestionException;

    protected abstract Ingredients digest(INGESTION ingestion) throws UserAgentDigestionException;

    public CompletableFuture<Ingredients> analyze(String rawUserAgent) throws UserAgentIngestionException, UserAgentDigestionException {
        return ingest(rawUserAgent)
                .thenApplyAsync((ingestion) -> ingestion
                        .map(this::digest)
                        .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results")))
                .toCompletableFuture();
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder {

        public abstract AsyncAnalyzer build();
    }

    @Override
    public void finalize() {
        this.teardown();
    }
}