package org.sheinbergon.useragent.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.processor.exception.ProcessorBuildException;
import org.sheinbergon.useragent.processor.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.processor.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.processor.util.UaParserJsUtils;
import org.sheinbergon.useragent.processor.util.V8Pool;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UaParserJsAsyncProcessor extends AsyncProcessor<UaParserJsIngestion> {

    public static Builder builder() {
        return new Builder();
    }

    private final V8Pool v8Pool;
    private final long v8AllocationTimeout;
    private final ExecutorService v8AllocationExecutor;
    private final ExecutorService ingestionExecutor;

    @Override
    public void teardown() {
        v8AllocationExecutor.shutdownNow();
        ingestionExecutor.shutdownNow();
        v8Pool.teardown();
    }

    @Override
    protected CompletionStage<Optional<UaParserJsIngestion>> ingest(final String userAgent) throws UserAgentIngestionException {
        return CompletableFuture.
                supplyAsync(() -> v8Pool.allocate(v8AllocationTimeout), v8AllocationExecutor).
                thenApplyAsync(v8 -> {
                    Optional<UaParserJsIngestion> result = UaParserJsUtils.ingest(v8, userAgent);
                    v8Pool.release(v8);
                    return result;
                }, ingestionExecutor);
    }

    @Override
    protected UserAgentIngredients digest(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        try {
            return UaParserJsUtils.digest(ingestion);
        } catch (RuntimeException rx) {
            throw new UserAgentDigestionException(rx);
        }
    }

    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends AsyncProcessor.Builder<UaParserJsIngestion, UaParserJsAsyncProcessor> {

        @Setter
        private int v8PoolSize = 16;
        @Setter
        private long v8AllocationTimeout = 200L;
        @Setter
        private int v8AllocationConcurrency = 16;
        @Setter
        private int ingestionConcurrency = 4;

        private void verify() {
            if (v8PoolSize <= 0) {
                throw new IllegalArgumentException("V8 pool size must be a positive integer");
            }
            if (v8AllocationTimeout <= 0) {
                throw new IllegalArgumentException("V8 allocation timeout must be a positive long integer");
            }
            if (v8AllocationConcurrency <= 0 || ingestionConcurrency <= 0) {
                throw new IllegalArgumentException("Concurrency values must be positive integers");
            }
        }

        @Override
        public UaParserJsAsyncProcessor build() {
            try {
                verify();

                V8Pool v8Pool = V8Pool.create(v8PoolSize, UaParserJsUtils.SCRIPTS);

                ExecutorService v8AllocationExecutor = Executors.newFixedThreadPool(v8AllocationConcurrency);
                ExecutorService ingestionExecutor = Executors.newFixedThreadPool(ingestionConcurrency);

                return new UaParserJsAsyncProcessor(v8Pool, v8AllocationTimeout, v8AllocationExecutor, ingestionExecutor);
            } catch (RuntimeException x) {
                throw new ProcessorBuildException(String.format("Could not build %s instance", UaParserJsAsyncProcessor.class), x);
            }
        }
    }
}
