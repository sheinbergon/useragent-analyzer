package org.sheinbergon.useragent.analyzer;

import org.junit.Assert;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.util.UaParserJsUtils;

import java.util.UUID;

import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_DIGESTION;
import static org.sheinbergon.useragent.analyzer.UaParserJsTestUtils.INGESTION;


public class DigestionTest {
    @Test
    public void unrecognizedDeviceDigestion() {
        Assert.assertEquals(UaParserJsUtils.digestDevice(UUID.randomUUID().toString()), UserAgentIngredients.Device.UNKNOWN);
    }

    @Test
    public void validDigestion() {
        Assert.assertEquals(UaParserJsUtils.digest(INGESTION), VALID_DIGESTION);
    }
}