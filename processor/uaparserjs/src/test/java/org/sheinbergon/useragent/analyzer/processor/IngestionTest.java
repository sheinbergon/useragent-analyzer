package org.sheinbergon.useragent.analyzer.processor;

import com.eclipsesource.v8.V8;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.analyzer.processor.util.V8Pool;
import org.sheinbergon.useragent.analyzer.processor.util.UaParserJsUtils;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class IngestionTest {

    private static V8 v8;
    private static V8Pool pool;

    @BeforeClass
    public static void setup() throws Exception {
        pool = V8Pool.create(UaParserJsTestUtils.POOL_SIZE, UaParserJsUtils.SCRIPTS);
        v8 = pool.allocate(UaParserJsTestUtils.POOL_ALLOCATION_TIMEOUT_MS);
    }

    @AfterClass
    public static void teardown() {
        Optional.ofNullable(v8)
                .ifPresent(runtime -> pool.release(runtime));
        pool.teardown();
    }

    @Test
    public void validIngestion() {
        UaParserJsIngestion ingestion = UaParserJsUtils
                .ingest(v8, ProcessorTestUtils.VALID_USER_AGENT)
                .orElseThrow(() -> new IllegalStateException("No ingestion returned"));
        Assert.assertEquals(ingestion, UaParserJsTestUtils.INGESTION);
    }

    @Test
    public void invalidIngestion() {
        UaParserJsIngestion ingestion = UaParserJsUtils
                .ingest(v8, ProcessorTestUtils.randomString())
                .orElseThrow(() -> new IllegalStateException("No ingestion returned"));
        Assert.assertEquals(ingestion, UaParserJsTestUtils.EMPTY_INGESTION);
    }
}
