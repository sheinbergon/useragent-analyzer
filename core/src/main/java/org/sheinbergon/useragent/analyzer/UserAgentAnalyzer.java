package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.analyzer.cache.Cache;
import org.sheinbergon.useragent.analyzer.cache.PseudoCache;
import org.sheinbergon.useragent.analyzer.exception.UserAgentAnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.processor.Processor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAgentAnalyzer {

    public static Builder builder() {
        return new Builder();
    }

    final Processor<?> processor;

    final Cache cache;

    public UserAgentIngredients analyze(final String userAgent) {
        UserAgentIngredients ingredients = cache.read(userAgent)
                .orElseGet(() -> processor.process(userAgent));
        cache.write(userAgent, ingredients);
        return ingredients;
    }

    public final void teardown() {
        processor.teardown();
    }

    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder {

        @Setter
        private Processor<?> processor;

        @Setter
        private Cache cache;

        public UserAgentAnalyzer build() {
            if (cache == null) {
                cache = PseudoCache.builder().build();
            }
            if (processor == null) {
                throw new UserAgentAnalyzerBuildException("Processor implementation not set");
            }
            return new UserAgentAnalyzer(processor, cache);
        }
    }
}