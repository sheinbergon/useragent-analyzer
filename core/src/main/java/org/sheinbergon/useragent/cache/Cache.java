package org.sheinbergon.useragent.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sheinbergon.useragent.Ingredients;

import java.util.Optional;

/**
 * @author Idan Sheinberg
 */
public abstract class Cache {

    public abstract Optional<Ingredients> read(String raw);

    public abstract void write(String raw, Ingredients ingredients);

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder<C extends Cache> {
        public abstract C build();
    }
}