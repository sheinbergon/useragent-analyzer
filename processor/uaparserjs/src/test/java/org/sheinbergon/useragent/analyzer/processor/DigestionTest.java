package org.sheinbergon.useragent.analyzer.processor;

import org.junit.Assert;
import org.junit.Test;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.processor.util.UaParserJsUtils;

import java.util.UUID;

import static org.sheinbergon.useragent.analyzer.processor.UaParserJsTestUtils.INGESTION;


public class DigestionTest {
    @Test
    public void unrecognizedDeviceDigestion() {
        Assert.assertEquals(UaParserJsUtils.digestDevice(UUID.randomUUID().toString()), UserAgentIngredients.Device.UNKNOWN);
    }

    @Test
    public void validDigestion() {
        Assert.assertEquals(UaParserJsUtils.digest(INGESTION), ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS);
    }
}