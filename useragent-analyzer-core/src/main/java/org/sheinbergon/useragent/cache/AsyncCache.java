package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Idan Sheinberg
 */
public abstract class AsyncCache {

    public abstract CompletableFuture<Optional<Ingredients>> read(String raw);

    public abstract CompletableFuture<Void> write(String raw, Ingredients ingredients);

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder {
        public abstract AsyncCache build();

        protected static AsyncCache wrap(final Cache cache) {
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
}