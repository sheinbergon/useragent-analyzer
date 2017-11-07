package org.sheinbergon.useragent;

import lombok.Builder;
import org.sheinbergon.useragent.processor.Processor;
import org.sheinbergon.useragent.cache.Cache;

@Builder(builderClassName = "Builder")
public class UserAgentAnalyzer {

    private final Processor<?> processor;

    private final Cache cache;

    public UserAgentIngredients analyze(final String userAgent) {
        UserAgentIngredients ingredients = cache.read(userAgent)
                .orElseGet(() -> processor.process(userAgent));

        cache.write(userAgent, ingredients);
        return ingredients;
    }
}