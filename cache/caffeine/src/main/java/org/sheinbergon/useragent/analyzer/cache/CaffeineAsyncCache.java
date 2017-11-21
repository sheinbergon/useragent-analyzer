package org.sheinbergon.useragent.analyzer.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * @author Idan Sheinberg
 */
// TODO - Add dedicated operation executor
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CaffeineAsyncCache extends AsyncCache {

    public static Builder builder() {
        return new Builder();
    }

    private AsyncLoadingCache<String, UserAgentIngredients> caffeine;

    private final int maxEntries;

    void setup() {
        caffeine = Caffeine.newBuilder().
                maximumSize(maxEntries).
                buildAsync(key -> null);
    }

    @Override
    public CompletableFuture<Optional<UserAgentIngredients>> read(String raw) {
        return Optional.ofNullable(caffeine.getIfPresent(raw))
                .map(future -> future.thenApply(Optional::ofNullable))
                .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public CompletableFuture<Void> write(String raw, UserAgentIngredients ingredients) {
        return CompletableFuture.runAsync(() -> caffeine.put(raw, CompletableFuture.completedFuture(ingredients)));
    }


    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends AsyncCache.Builder<CaffeineAsyncCache> {

        @Setter
        private int maxEntries = 100000;

        @Override
        public CaffeineAsyncCache build() {
            CaffeineAsyncCache cache = new CaffeineAsyncCache(maxEntries);
            cache.setup();
            return cache;
        }
    }
}
