package org.sheinbergon.useragent.analyzer.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentIngestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @param <INGESTION> Analysis result object-graph root
 * @author Idan Sheinberg
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AsyncProcessor<INGESTION> {

    protected abstract CompletionStage<Optional<INGESTION>> ingest(String userAgent) throws UserAgentIngestionException;

    protected abstract UserAgentIngredients digest(INGESTION ingestion) throws UserAgentDigestionException;

    public CompletableFuture<UserAgentIngredients> process(String userAgent) throws UserAgentIngestionException, UserAgentDigestionException {
        return ingest(userAgent)
                .thenApplyAsync((ingestion) -> ingestion
                        .map(this::digest)
                        .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results")))
                .toCompletableFuture();
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder<INGESTION, A extends AsyncProcessor<INGESTION>> {
        public abstract A build();
    }

    @Override
    public void finalize() {
        this.teardown();
    }
}