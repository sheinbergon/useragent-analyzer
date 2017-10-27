package org.sheinbergon.useragent.analyzer;

import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.UUID;

public class TestUtils {

    public final static long POOL_ALLOCATION_TIMEOUT_MS = 100L;

    public final static int POOL_SIZE = 1;

    final static String VALID_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.1.1; ONEPLUS A5000 Build/NMF26X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Mobile Safari/537.36";

    final static UaParserJsIngestion INGESTION = new UaParserJsIngestion()
            .setBrowser(new UaParserJsIngestion.Browser().setName("Chrome").setVersion("56.0.2924.87"))
            .setDevice(new UaParserJsIngestion.Device().setType("mobile"))
            .setEngine(new UaParserJsIngestion.Engine().setName("WebKit").setVersion("537.36"))
            .setOs(new UaParserJsIngestion.Os().setName("Android").setVersion("7.1.1"))
            .setCpu(new UaParserJsIngestion.Cpu());

    final static UserAgentIngredients DIGESTION = UserAgentIngredients.builder()
            .browserName("Chrome")
            .browserVersion("56.0.2924.87")
            .deviceType(UserAgentIngredients.Device.MOBILE)
            .osName("Android")
            .osVersion("7.1.1")
            .renderingEngineName("WebKit")
            .renderingEngineVersion("537.36")
            .build();

    final static UaParserJsIngestion EMPTY_INGESTION = new UaParserJsIngestion()
            .setBrowser(new UaParserJsIngestion.Browser())
            .setDevice(new UaParserJsIngestion.Device())
            .setEngine(new UaParserJsIngestion.Engine())
            .setOs(new UaParserJsIngestion.Os())
            .setCpu(new UaParserJsIngestion.Cpu());

    static String randomString() {
        return UUID.randomUUID().toString();
    }
}
