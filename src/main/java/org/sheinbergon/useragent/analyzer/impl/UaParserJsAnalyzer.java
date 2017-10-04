package org.sheinbergon.useragent.analyzer.impl;

import com.eclipsesource.v8.V8;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.analyzer.Analyzer;
import org.sheinbergon.useragent.analyzer.exception.AnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.impl.util.UaParserJsUtils;
import org.sheinbergon.useragent.analyzer.impl.util.V8Pool;
import org.sheinbergon.useragent.cache.Cache;

import java.util.Optional;

public class UaParserJsAnalyzer extends Analyzer<UaParserJsIngestion> {

    public static Builder builder() {
        return new Builder();
    }

    private final V8Pool v8Pool;

    private UaParserJsAnalyzer(Cache cache, V8Pool v8Pool) {
        super(cache);
        this.v8Pool = v8Pool;
    }

    @Override
    public void teardown() {
        v8Pool.teardown();
    }

    @Override
    protected Optional<UaParserJsIngestion> ingest(String rawUserAgent) throws UserAgentIngestionException {
        V8 v8 = null;
        try {
            v8 = v8Pool.allocate();
            return UaParserJsUtils.v8ScriptExecute(v8, rawUserAgent)
                    .map(UaParserJsUtils::jsonDeserialize);
        } finally {
            Optional.ofNullable(v8)
                    .ifPresent(v8Pool::release);
        }
    }

    @Override
    protected Ingredients digest(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        try {
            return UaParserJsUtils.toIngredients(ingestion);
        } catch (RuntimeException rx) {
            throw new UserAgentDigestionException(rx);
        }
    }

    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends Analyzer.Builder<Builder> {

        @Setter
        private int v8RuntimeInstances = 10;

        @Override
        protected UaParserJsAnalyzer.Builder getSelf() {
            return this;
        }

        @Override
        public UaParserJsAnalyzer build() {
            try {
                V8Pool v8Pool = V8Pool.create(v8RuntimeInstances, UaParserJsUtils.scriptPaths());
                return new UaParserJsAnalyzer(cache, v8Pool);
            } catch (RuntimeException x) {
                throw new AnalyzerBuildException(String.format("Could not build %s instance", UaParserJsAsyncAnalyzer.class), x);
            }
        }
    }
}
