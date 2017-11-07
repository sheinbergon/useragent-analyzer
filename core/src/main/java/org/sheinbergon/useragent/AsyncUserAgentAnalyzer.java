package org.sheinbergon.useragent;

import lombok.Builder;
import org.sheinbergon.useragent.processor.AsyncProcessor;
import org.sheinbergon.useragent.cache.AsyncCache;

import java.util.concurrent.CompletableFuture;

@Builder(builderClassName = "Builder")
public class AsyncUserAgentAnalyzer {

    private final AsyncProcessor<?> processor;

    private final AsyncCache cache;

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
}