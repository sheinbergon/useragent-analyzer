package org.sheinbergon.useragent.cache.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.cache.AsyncCache;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncPseudoCache {

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