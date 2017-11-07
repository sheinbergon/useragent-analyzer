package org.sheinbergon.useragent.processor;

public class UaParserJsTestUtils {

    public final static long POOL_ALLOCATION_TIMEOUT_MS = 100L;

    public final static int POOL_SIZE = 1;

    final static UaParserJsIngestion INGESTION = new UaParserJsIngestion()
            .setBrowser(new UaParserJsIngestion.Browser().setName("Chrome").setVersion("56.0.2924.87"))
            .setDevice(new UaParserJsIngestion.Device().setType("mobile"))
            .setEngine(new UaParserJsIngestion.Engine().setName("WebKit").setVersion("537.36"))
            .setOs(new UaParserJsIngestion.Os().setName("Android").setVersion("7.1.1"))
            .setCpu(new UaParserJsIngestion.Cpu());

    final static UaParserJsIngestion EMPTY_INGESTION = new UaParserJsIngestion()
            .setBrowser(new UaParserJsIngestion.Browser())
            .setDevice(new UaParserJsIngestion.Device())
            .setEngine(new UaParserJsIngestion.Engine())
            .setOs(new UaParserJsIngestion.Os())
            .setCpu(new UaParserJsIngestion.Cpu());
}
