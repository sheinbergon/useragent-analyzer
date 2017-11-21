package org.sheinbergon.useragent.analyzer.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentIngestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                .thenApply((ingestion) -> ingestion
                        .map(this::digest)
                        .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results")))
                .toCompletableFuture();
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder<INGESTION, A extends AsyncProcessor<INGESTION>> {
        public abstract A build();

        static <I> AsyncProcessor<I> wrap(final Processor<I> processor, final int ingestionConcurrecy) {
            return new AsyncProcessor<I>() {

                private final ExecutorService ingestionExecutor = Executors.newFixedThreadPool(ingestionConcurrecy);

                @Override
                protected CompletionStage<Optional<I>> ingest(String userAgent) throws UserAgentIngestionException {
                    return CompletableFuture.supplyAsync(() -> processor.ingest(userAgent), ingestionExecutor);
                }

                @Override
                protected UserAgentIngredients digest(I ingestion) throws UserAgentDigestionException {
                    return processor.digest(ingestion);
                }

                @Override
                public void teardown() {
                    ingestionExecutor.shutdownNow();
                }
            };
        }
    }

    @Override
    public void finalize() {
        this.teardown();
    }
}