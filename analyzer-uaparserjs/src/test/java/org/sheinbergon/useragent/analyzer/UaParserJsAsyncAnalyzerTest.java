package org.sheinbergon.useragent.analyzer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.*;

// TODO - Add invalid build values tests
public class UaParserJsAsyncAnalyzerTest {

    @BeforeClass
    public static void setup() {
        analyzer = UaParserJsAsyncAnalyzer.builder().build();
    }

    @AfterClass
    public static void teardown() {
        analyzer.teardown();
    }

    private static UaParserJsAsyncAnalyzer analyzer = null;

    @Test
    public void validAnalysis() throws ExecutionException, InterruptedException {
        analyzer.analyze(VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, VALID_DIGESTION))
                .get();
    }

    @Test
    public void invalidInputAnalysis() throws ExecutionException, InterruptedException {
        analyzer.analyze(randomString())
                .thenAccept(ingredients -> assertEquals(ingredients, UserAgentIngredients.EMPTY))
                .get();
    }
}
