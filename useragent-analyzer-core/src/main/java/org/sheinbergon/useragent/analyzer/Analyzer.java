package org.sheinbergon.useragent.analyzer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.cache.Cache;
import org.sheinbergon.useragent.cache.impl.PseudoCache;

import java.util.Optional;

/**
 * @param <INGESTION> Analysis result object-graph root
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor
public abstract class Analyzer<INGESTION> {

    private final Cache cache;

    protected abstract Optional<INGESTION> ingest(String rawUserAgent) throws UserAgentIngestionException;

    protected abstract Ingredients digest(INGESTION ingestion) throws UserAgentDigestionException;

    public Ingredients analyze(String rawUserAgent) throws UserAgentIngestionException, UserAgentDigestionException {
        Ingredients digest = cache.read(rawUserAgent)
                .orElseGet(() -> ingest(rawUserAgent)
                        .map(this::digest)
                        .orElseThrow(() -> new UserAgentIngestionException("Ingestion provided no results")));

        cache.write(rawUserAgent, digest);
        return digest;
    }

    public abstract void teardown();

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static abstract class Builder<IMPL extends Builder<IMPL>> {

        protected Cache cache = PseudoCache.builder().build();

        public IMPL cache(Cache cache) {
            this.cache = cache;
            return getSelf();
        }

        protected abstract IMPL getSelf();

        public abstract Analyzer build();
    }


    @Override
    public void finalize() {
        this.teardown();
    }
}
