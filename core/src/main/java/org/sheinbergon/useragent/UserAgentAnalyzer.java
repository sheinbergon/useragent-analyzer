package org.sheinbergon.useragent;

import lombok.Builder;
import org.sheinbergon.useragent.analyzer.Analyzer;
import org.sheinbergon.useragent.cache.Cache;

@Builder(builderClassName = "Builder")
public class UserAgentAnalyzer {

    private final Analyzer<?> analyzer;

    private final Cache cache;

    public UserAgentIngredients process(final String userAgent) {
        UserAgentIngredients ingredients = cache.read(userAgent)
                .orElseGet(() -> analyzer.analyze(userAgent));

        cache.write(userAgent, ingredients);
        return ingredients;
    }
}