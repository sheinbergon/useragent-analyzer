package org.sheinbergon.useragent;

import lombok.Builder;
import org.sheinbergon.useragent.analyzer.AsyncAnalyzer;
import org.sheinbergon.useragent.cache.AsyncCache;

import java.util.concurrent.CompletableFuture;

@Builder
public class AsyncUserAgentAnalyzer {

    private final AsyncAnalyzer<?> analyzer;

    private final AsyncCache cache;

    public CompletableFuture<Ingredients> process(final String userAgent) {
        return cache.read(userAgent)
                .thenComposeAsync(cached -> cached
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> analyzer
                                .analyze(userAgent)
                                .whenComplete((ingredients, throwable) -> cache.write(userAgent, ingredients)))
                );
    }
}