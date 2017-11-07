package org.sheinbergon.useragent.processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.sheinbergon.useragent.processor.ProcessorTestUtils.*;

// TODO - Add invalid build values tests
public class UaParserJsAsyncProcessorTest {

    @BeforeClass
    public static void setup() {
        processor = UaParserJsAsyncProcessor.builder().build();
    }

    @AfterClass
    public static void teardown() {
        processor.teardown();
    }

    private static UaParserJsAsyncProcessor processor = null;

    @Test
    public void validAnalysis() throws ExecutionException, InterruptedException {
        processor.process(VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS))
                .get();
    }

    @Test
    public void invalidInputAnalysis() throws ExecutionException, InterruptedException {
        processor.process(randomString())
                .thenAccept(ingredients -> assertEquals(ingredients, UserAgentIngredients.EMPTY))
                .get();
    }
}
