package org.sheinbergon.useragent;

import lombok.Builder;
import org.sheinbergon.useragent.analyzer.Analyzer;
import org.sheinbergon.useragent.cache.Cache;

@Builder(builderClassName = "Builder")
public class UserAgentAnalyzer {

    private final Analyzer<?> analyzer;

    private final Cache cache;

    public Ingredients process(final String userAgent) {
        Ingredients ingredients = cache.read(userAgent)
                .orElseGet(() -> analyzer.analyze(userAgent));

        cache.write(userAgent, ingredients);
        return ingredients;
    }
}