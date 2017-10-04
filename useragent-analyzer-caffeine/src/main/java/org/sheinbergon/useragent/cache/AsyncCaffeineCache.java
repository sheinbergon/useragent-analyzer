package org.sheinbergon.useragent.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.cache.AsyncCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncCaffeineCache extends AsyncCache {

    public static Builder builder() {
        return new Builder();
    }

    private AsyncLoadingCache<String, Ingredients> caffeine;

    private final int maxEntries;

    public void setup() {
        caffeine = Caffeine.newBuilder().
                maximumSize(maxEntries).
                buildAsync(key -> null);
    }

    @Override
    public CompletableFuture<Optional<Ingredients>> read(String raw) {
        return Optional.ofNullable(caffeine.getIfPresent(raw))
                .map(future -> future.thenApply(Optional::ofNullable))
                .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public CompletableFuture<Void> write(String raw, Ingredients ingredients) {
        return CompletableFuture.supplyAsync(() -> {
            caffeine.put(raw, CompletableFuture.completedFuture(ingredients));
            return null;
        });
    }


    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends AsyncCache.Builder {

        @Setter
        private int maxEntries = 100000;

        @Override
        public AsyncCache build() {
            AsyncCaffeineCache cache = new AsyncCaffeineCache(maxEntries);
            cache.setup();
            return cache;
        }
    }
}
