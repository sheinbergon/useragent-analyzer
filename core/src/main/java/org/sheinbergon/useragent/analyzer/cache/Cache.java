package org.sheinbergon.useragent.analyzer.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

import java.util.Optional;

/**
 * @author Idan Sheinberg
 */
public abstract class Cache {

    public abstract Optional<UserAgentIngredients> read(String raw);

    public abstract void write(String raw, UserAgentIngredients ingredients);

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder<C extends Cache> {
        public abstract C build();
    }
}