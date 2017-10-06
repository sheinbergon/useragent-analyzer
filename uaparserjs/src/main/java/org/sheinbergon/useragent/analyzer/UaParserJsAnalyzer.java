package org.sheinbergon.useragent.analyzer;

import com.eclipsesource.v8.V8;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.exception.AnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.util.UaParserJsUtils;
import org.sheinbergon.useragent.analyzer.util.V8Pool;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UaParserJsAnalyzer extends Analyzer<UaParserJsIngestion> {

    public static Builder builder() {
        return new Builder();
    }

    private final V8Pool v8Pool;
    private final long v8AllocationTimeout;


    @Override
    public void teardown() {
        v8Pool.teardown();
    }

    @Override
    protected Optional<UaParserJsIngestion> ingest(String userAgent) throws UserAgentIngestionException {
        V8 v8 = null;
        try {
            v8 = v8Pool.allocate(v8AllocationTimeout);
            return UaParserJsUtils.ingest(v8, userAgent);
        } finally {
            Optional.ofNullable(v8)
                    .ifPresent(v8Pool::release);
        }
    }

    @Override
    protected UserAgentIngredients digest(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        return UaParserJsUtils.digest(ingestion);
    }

    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder extends Analyzer.Builder<UaParserJsIngestion, UaParserJsAnalyzer> {

        @Setter
        private int v8PoolSize = 16;

        @Setter
        private long v8AllocationTimeout = 200L;

        private void verify() {
            if (v8PoolSize <= 0) {
                throw new IllegalArgumentException("V8 pool size must be a positive integer");
            }
            if (v8AllocationTimeout <= 0) {
                throw new IllegalArgumentException("V8 allocation timeout must be a positive long integer");
            }
        }

        @Override
        public UaParserJsAnalyzer build() {
            try {
                verify();
                return new UaParserJsAnalyzer(V8Pool.create(v8PoolSize, UaParserJsUtils.SCRIPTS), v8AllocationTimeout);
            } catch (RuntimeException x) {
                throw new AnalyzerBuildException(String.format("Could not build %s instance", UaParserJsAsyncAnalyzer.class), x);
            }
        }
    }
}
