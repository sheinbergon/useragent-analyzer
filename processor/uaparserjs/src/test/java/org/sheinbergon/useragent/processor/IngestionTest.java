package org.sheinbergon.useragent.processor;

import com.eclipsesource.v8.V8;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.processor.util.UaParserJsUtils;
import org.sheinbergon.useragent.processor.util.V8Pool;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.sheinbergon.useragent.processor.ProcessorTestUtils.VALID_USER_AGENT;
import static org.sheinbergon.useragent.processor.ProcessorTestUtils.randomString;
import static org.sheinbergon.useragent.processor.UaParserJsTestUtils.*;

public class IngestionTest {

    private static V8 v8;
    private static V8Pool pool;

    @BeforeClass
    public static void setup() throws Exception {
        pool = V8Pool.create(POOL_SIZE, UaParserJsUtils.SCRIPTS);
        v8 = pool.allocate(POOL_ALLOCATION_TIMEOUT_MS);
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
                .ingest(v8, VALID_USER_AGENT)
                .orElseThrow(() -> new IllegalStateException("No ingestion returned"));
        assertEquals(ingestion, INGESTION);
    }

    @Test
    public void invalidIngestion() {
        UaParserJsIngestion ingestion = UaParserJsUtils
                .ingest(v8, randomString())
                .orElseThrow(() -> new IllegalStateException("No ingestion returned"));
        assertEquals(ingestion, EMPTY_INGESTION);
    }
}
