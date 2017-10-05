package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;

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
    public static class Builder extends Cache.Builder<PseudoCache> {
        @Override
        public PseudoCache build() {
            return new PseudoCache();
        }
    }
}