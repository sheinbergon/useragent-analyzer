package org.sheinbergon.useragent.processor;

import org.junit.Assert;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.processor.util.UaParserJsUtils;

import java.util.UUID;

import static org.sheinbergon.useragent.processor.ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS;
import static org.sheinbergon.useragent.processor.UaParserJsTestUtils.INGESTION;


public class DigestionTest {
    @Test
    public void unrecognizedDeviceDigestion() {
        Assert.assertEquals(UaParserJsUtils.digestDevice(UUID.randomUUID().toString()), UserAgentIngredients.Device.UNKNOWN);
    }

    @Test
    public void validDigestion() {
        Assert.assertEquals(UaParserJsUtils.digest(INGESTION), VALID_USER_AGENT_INGREDIENTS);
    }
}