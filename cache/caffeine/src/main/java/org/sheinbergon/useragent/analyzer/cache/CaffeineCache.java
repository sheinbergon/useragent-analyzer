package org.sheinbergon.useragent.analyzer.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

import java.util.Optional;


/**
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CaffeineCache extends Cache {

    public static Builder builder() {
        return new Builder();
    }

    private com.github.benmanes.caffeine.cache.Cache<String, UserAgentIngredients> caffeine;

    private final int maxEntries;

    private void setup() {
        caffeine = Caffeine.newBuilder().
                maximumSize(maxEntries).
                build();
    }

    public void teardown() {
        caffeine.invalidateAll();
        caffeine.cleanUp();
    }

    @Override
    public Optional<UserAgentIngredients> read(String raw) {
        return Optional.ofNullable(caffeine.getIfPresent(raw));
    }

    @Override
    public void write(String raw, UserAgentIngredients ingredients) {
        caffeine.put(raw, ingredients);
    }


    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends Cache.Builder<CaffeineCache> {

        @Setter
        private int maxEntries = 100000;

        @Override
        public CaffeineCache build() {
            CaffeineCache cache = new CaffeineCache(maxEntries);
            cache.setup();
            return cache;
        }
    }
}
