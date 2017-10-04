package org.sheinbergon.useragent.cache;

import org.sheinbergon.useragent.Ingredients;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Idan Sheinberg
 */
public interface AsyncCache {

    CompletableFuture<Optional<Ingredients>> read(String raw);

    CompletableFuture<Void> write(String raw, Ingredients ingredients);

    static AsyncCache wrap(final Cache cache) {
        return new AsyncCache() {
            @Override
            public CompletableFuture<Optional<Ingredients>> read(String raw) {
                return CompletableFuture.supplyAsync(() -> cache.read(raw));
            }

            @Override
            public CompletableFuture<Void> write(String raw, Ingredients ingredients) {
                return CompletableFuture.runAsync(() -> cache.write(raw, ingredients));
            }
        };
    }
}