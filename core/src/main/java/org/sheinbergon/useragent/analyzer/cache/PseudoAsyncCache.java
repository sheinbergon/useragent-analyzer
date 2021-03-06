package org.sheinbergon.useragent.analyzer.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PseudoAsyncCache {

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends AsyncCache.Builder {
        @Override
        public AsyncCache build() {
            return Builder.wrap(PseudoCache.builder().build());
        }
    }
}