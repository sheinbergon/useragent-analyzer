package org.sheinbergon.useragent.analyzer.processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

import static org.junit.Assert.assertEquals;
import static org.sheinbergon.useragent.analyzer.processor.ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS;
import static org.sheinbergon.useragent.analyzer.processor.ProcessorTestUtils.randomString;


// TODO - Add invalid build values tests
public class UaParserJsProcessorTest {

    @BeforeClass
    public static void setup() {
        processor = UaParserJsProcessor.builder().build();
    }

    @AfterClass
    public static void teardown() {
        processor.teardown();
    }

    private static UaParserJsProcessor processor = null;

    @Test
    public void validAnalysis() {
        assertEquals(processor.process(ProcessorTestUtils.VALID_USER_AGENT), VALID_USER_AGENT_INGREDIENTS);
    }

    @Test
    public void invalidInputAnalysis() {
        assertEquals(processor.process(randomString()), UserAgentIngredients.EMPTY);
    }
}
