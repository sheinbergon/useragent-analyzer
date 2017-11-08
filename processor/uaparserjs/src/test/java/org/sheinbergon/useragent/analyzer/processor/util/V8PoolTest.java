package org.sheinbergon.useragent.analyzer.processor.util;

import com.eclipsesource.v8.V8;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.lambda.Unchecked;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sheinbergon.useragent.analyzer.processor.exception.V8PoolSetupException;
import org.sheinbergon.useragent.analyzer.processor.exception.V8ResourceAllocationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.sheinbergon.useragent.analyzer.processor.UaParserJsTestUtils.POOL_ALLOCATION_TIMEOUT_MS;
import static org.sheinbergon.useragent.analyzer.processor.UaParserJsTestUtils.POOL_SIZE;

public class V8PoolTest {

    private final static ObjectReader JACKSON_OBJECT_READER = UaParserJsUtils.JACKSON_OBJECT_READER.forType(ObjectNode.class);

    private final static String TEST_FUNCTIONS_PATH = "test-functions.js";
    private final static String NON_EXISTING_SCRIPT = "non/existing/script.js";


    @Before
    public void setup() throws Exception {
        pool = V8Pool.create(POOL_SIZE, new String[]{TEST_FUNCTIONS_PATH});
        v8 = pool.allocate(POOL_ALLOCATION_TIMEOUT_MS);
    }

    @After
    public void teardown() {
        Optional.ofNullable(v8)
                .ifPresent(runtime -> pool.release(runtime));
        pool.teardown();
    }

    private V8 v8;
    private V8Pool pool;

    @Test(expected = V8ResourceAllocationException.class)
    public void poolExhaustionTest() {
        pool.allocate(POOL_ALLOCATION_TIMEOUT_MS);
    }

    @Test
    public void poolBorrowTest() {
        assertNotNull(v8);
    }

    @Test
    public void poolReturnTest() {
        pool.release(v8);
        v8 = pool.allocate(POOL_ALLOCATION_TIMEOUT_MS);
        assertNotNull(v8);
    }

    @Test
    public void functionExecution() {
        final String random = UUID.randomUUID().toString();
        Optional<String> result = UaParserJsUtils
                .executeV8Function(v8, random)
                .map(Unchecked.function(json -> (ObjectNode) JACKSON_OBJECT_READER.readValue(json)))
                .filter(node -> node.has("passed"))
                .map(node -> node.get("passed").asText())
                .filter(passed -> passed.equals(random));
        assertTrue(result.isPresent());
    }

    @Test(expected = V8PoolSetupException.class)
    public void nonExistingScriptPoolCreation() {
        V8Pool.create(POOL_SIZE, new String[]{NON_EXISTING_SCRIPT});
    }

}
