package org.sheinbergon.useragent.analyzer.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.AsyncAnalyzer;
import org.sheinbergon.useragent.analyzer.exception.AnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.impl.util.UaParserJsUtils;
import org.sheinbergon.useragent.analyzer.impl.util.V8Pool;
import org.sheinbergon.useragent.cache.AsyncCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Idan Sheinberg
 */

public class UaParserJsAsyncAnalyzer extends AsyncAnalyzer<UaParserJsIngestion> {

    public static Builder builder() {
        return new Builder();
    }

    private final V8Pool v8Pool;
    private final ExecutorService v8AllocationExecutor;
    private final ExecutorService v8ExecutionExecutor;
    private final ExecutorService deserializationExecutor;

    private UaParserJsAsyncAnalyzer(AsyncCache cache, V8Pool v8Pool, ExecutorService v8AllocationExecutor, ExecutorService v8ExecutionExecutor, ExecutorService jacksonDeserializationExecutor) {
        super(cache);
        this.v8Pool = v8Pool;
        this.v8AllocationExecutor = v8AllocationExecutor;
        this.v8ExecutionExecutor = v8ExecutionExecutor;
        this.deserializationExecutor = jacksonDeserializationExecutor;
    }

    @Override
    public void teardown() {
        v8AllocationExecutor.shutdownNow();
        v8ExecutionExecutor.shutdownNow();
        deserializationExecutor.shutdownNow();
        v8Pool.teardown();
    }

    @Override
    protected CompletionStage<Optional<UaParserJsIngestion>> ingest(final String userAgent) throws UserAgentIngestionException {
        return CompletableFuture.
                supplyAsync(v8Pool::allocate, v8AllocationExecutor).
                thenApplyAsync(v8 -> {
                    Optional<String> result = UaParserJsUtils.v8ScriptExecute(v8, userAgent);
                    v8Pool.release(v8);
                    return result;
                }, v8ExecutionExecutor).
                thenApplyAsync(result -> result.map(UaParserJsUtils::jsonDeserialize), deserializationExecutor);
    }

    @Override
    protected Ingredients digest(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        try {
            return UaParserJsUtils.toIngredients(ingestion);
        } catch (RuntimeException rx) {
            throw new UserAgentDigestionException(rx);
        }
    }

    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends AsyncAnalyzer.Builder<Builder> {

        @Setter
        private int v8RuntimeInstances = 10;
        @Setter
        private int v8AllocationConcurrency = 16;
        @Setter
        private int v8ExecutionConcurrency = 4;
        @Setter
        private int deserializationConcurrency = 4;

        @Override
        protected Builder getSelf() {
            return this;
        }

        @Override
        public UaParserJsAsyncAnalyzer build() {

            try {
                V8Pool v8Pool = V8Pool.create(v8RuntimeInstances, UaParserJsUtils.scriptPaths());

                ExecutorService v8AllocationExecutor = Executors.newFixedThreadPool(v8AllocationConcurrency);
                ExecutorService v8ExecutionExecutor = Executors.newFixedThreadPool(v8ExecutionConcurrency);
                ExecutorService deserializationExecutor = Executors.newFixedThreadPool(deserializationConcurrency);

                return new UaParserJsAsyncAnalyzer(cache, v8Pool, v8AllocationExecutor, v8ExecutionExecutor, deserializationExecutor);
            } catch (RuntimeException x) {
                throw new AnalyzerBuildException(String.format("Could not build %s instance", UaParserJsAsyncAnalyzer.class), x);
            }
        }
    }
}
