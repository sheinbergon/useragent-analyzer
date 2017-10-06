package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Idan Sheinberg
 */
public abstract class AsyncCache {

    public abstract CompletableFuture<Optional<UserAgentIngredients>> read(String raw);

    public abstract CompletableFuture<Void> write(String raw, UserAgentIngredients ingredients);

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder<C extends AsyncCache> {
        public abstract C build();

        protected static AsyncCache wrap(final Cache cache) {
            return new AsyncCache() {
                @Override
                public CompletableFuture<Optional<UserAgentIngredients>> read(String raw) {
                    return CompletableFuture.supplyAsync(() -> cache.read(raw));
                }

                @Override
                public CompletableFuture<Void> write(String raw, UserAgentIngredients ingredients) {
                    return CompletableFuture.runAsync(() -> cache.write(raw, ingredients));
                }
            };
        }
    }
}