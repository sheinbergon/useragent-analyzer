package org.sheinbergon.useragent.analyzer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;

import static org.junit.Assert.assertEquals;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_DIGESTION;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.randomString;


// TODO - Add invalid build values tests
public class UaParserJsAnalyzerTest {

    @BeforeClass
    public static void setup() {
        analyzer = UaParserJsAnalyzer.builder().build();
    }

    @AfterClass
    public static void teardown() {
        analyzer.teardown();
    }

    private static UaParserJsAnalyzer analyzer = null;

    @Test
    public void validAnalysis() {
        assertEquals(analyzer.analyze(AnalyzerTestUtils.VALID_USER_AGENT), VALID_DIGESTION);
    }

    @Test
    public void invalidInputAnalysis() {
        assertEquals(analyzer.analyze(randomString()), UserAgentIngredients.EMPTY);
    }
}
