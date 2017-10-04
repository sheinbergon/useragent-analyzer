package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.cache.impl.CaffeineCache;
import org.sheinbergon.useragent.cache.impl.PseudoCache;

import java.util.Optional;

/**
 * @author Idan Sheinberg
 */
public abstract class Cache {

    public abstract Optional<Ingredients> read(String raw);

    public abstract void write(String raw, Ingredients ingredients);

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder {
        public abstract Cache build();
    }

    public static class Builders {
        public static PseudoCache.Builder psuedo() {
            return PseudoCache.builder();
        }

        public static CaffeineCache.Builder caffeine() {
            return CaffeineCache.builder();
        }
    }
}