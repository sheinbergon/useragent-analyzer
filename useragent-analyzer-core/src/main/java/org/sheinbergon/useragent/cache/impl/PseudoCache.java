package org.sheinbergon.useragent.cache.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.cache.Cache;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PseudoCache extends Cache {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Optional<Ingredients> read(String raw) {
        return Optional.empty();
    }

    @Override
    public void write(String raw, Ingredients ingredients) {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends Cache.Builder {
        @Override
        public Cache build() {
            return new PseudoCache();
        }
    }
}