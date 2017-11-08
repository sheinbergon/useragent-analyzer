package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.analyzer.cache.AsyncCache;
import org.sheinbergon.useragent.analyzer.cache.PseudoAsyncCache;
import org.sheinbergon.useragent.analyzer.exception.UserAgentAnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.processor.AsyncProcessor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncUserAgentAnalyzer {

    public static Builder builder() {
        return new Builder();
    }

    final AsyncProcessor<?> processor;

    final AsyncCache cache;

    // TODO - Maybe add a dedicated executor to the processor ?
    public CompletableFuture<UserAgentIngredients> analyze(final String userAgent) {
        return cache.read(userAgent)
                .thenComposeAsync(cached -> cached
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> processor
                                .process(userAgent)
                                .whenComplete((ingredients, throwable) -> cache.write(userAgent, ingredients)))
                );
    }

    public final void teardown() {
        processor.teardown();
    }

    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder {

        @Setter
        private AsyncProcessor<?> processor;

        @Setter
        private AsyncCache cache;

        public AsyncUserAgentAnalyzer build() {
            if (cache == null) {
                cache = PseudoAsyncCache.builder().build();
            }
            if (processor == null) {
                throw new UserAgentAnalyzerBuildException("Processor implementation not set");
            }
            return new AsyncUserAgentAnalyzer(processor, cache);
        }
    }
}