package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PseudoCache extends Cache {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Optional<UserAgentIngredients> read(String raw) {
        return Optional.empty();
    }

    @Override
    public void write(String raw, UserAgentIngredients ingredients) {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends Cache.Builder<PseudoCache> {
        @Override
        public PseudoCache build() {
            return new PseudoCache();
        }
    }
}