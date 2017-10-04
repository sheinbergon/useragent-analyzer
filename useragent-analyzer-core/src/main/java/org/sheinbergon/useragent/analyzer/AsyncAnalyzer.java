package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.impl.UaParserJsAsyncAnalyzer;
import org.sheinbergon.useragent.cache.AsyncCache;
import org.sheinbergon.useragent.cache.impl.AsyncPseudoCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @param <INGESTION> Analysis result object-graph root
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor
public abstract class AsyncAnalyzer<INGESTION> {

    private final AsyncCache cache;

    protected abstract CompletionStage<Optional<INGESTION>> ingest(String rawUserAgent) throws UserAgentIngestionException;

    protected abstract Ingredients digest(INGESTION ingestion) throws UserAgentDigestionException;

    public CompletableFuture<Ingredients> analyze(String rawUserAgent) throws UserAgentIngestionException, UserAgentDigestionException {
        return cache.read(rawUserAgent)
                .thenComposeAsync(ingredients -> ingredients
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> ingest(rawUserAgent)
                                .thenApplyAsync((ingestion) -> ingestion
                                        .map(this::digest)
                                        .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results")))
                                .whenComplete((digest, throwable) -> cache.write(rawUserAgent, digest))
                                .toCompletableFuture()
                        )
                );
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder<IMPL extends Builder<IMPL>> {

        protected AsyncCache cache = AsyncPseudoCache.builder().build();

        public IMPL cache(AsyncCache cache) {
            this.cache = cache;
            return getSelf();
        }

        protected abstract IMPL getSelf();

        public abstract AsyncAnalyzer build();
    }

    @Override
    public void finalize() {
        this.teardown();
    }

    public static class Builders {
        public static UaParserJsAsyncAnalyzer.Builder uaParserJs() {
            return UaParserJsAsyncAnalyzer.builder();
        }
    }
}