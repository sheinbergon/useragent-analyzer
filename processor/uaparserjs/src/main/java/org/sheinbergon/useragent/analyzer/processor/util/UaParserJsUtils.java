package org.sheinbergon.useragent.analyzer.processor.util;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Locker;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.processor.UaParserJsIngestion;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentIngestionException;

import java.io.IOException;
import java.util.Optional;

public class UaParserJsUtils {

    private final static String UA_PARSER_JS = "ua-parser.js";
    private final static String WRAPPER = "ua-parser-j2v8-wrapper.js";
    private final static String WRAPPER_FUNCTION = "Wrapper";

    final static ObjectReader JACKSON_OBJECT_READER = new ObjectMapper().
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).
            reader().
            forType(UaParserJsIngestion.class);

    public final static String[] SCRIPTS = new String[]{UA_PARSER_JS, WRAPPER};

    public static UserAgentIngredients digest(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        try {
            UserAgentIngredients.Builder builder = UserAgentIngredients.builder();

            Optional.ofNullable(ingestion.getBrowser())
                    .ifPresent(browser -> builder
                            .browserName(browser.getName())
                            .browserVersion(browser.getVersion()));

            Optional.ofNullable(ingestion.getEngine())
                    .ifPresent(engine -> builder
                            .renderingEngineName(engine.getName())
                            .renderingEngineVersion(engine.getVersion()));

            Optional.ofNullable(ingestion.getOs())
                    .ifPresent(os -> builder
                            .osName(os.getName())
                            .osVersion(os.getVersion()));

            Optional.ofNullable(ingestion.getDevice())
                    .ifPresent(device -> builder
                            .deviceMake(device.getVendor())
                            .deviceModel(device.getModel())
                            .deviceType(digestDevice(device.getType())));

            Optional.ofNullable(ingestion.getCpu())
                    .ifPresent(cpu -> builder
                            .cpuArchitecture(cpu.getArchitecture()));

            return builder.build();
        } catch (RuntimeException rx) {
            throw new UserAgentDigestionException(rx);
        }
    }

    public static Optional<UaParserJsIngestion> ingest(V8 v8, String userAgent) {
        try {
            return executeV8Function(v8, userAgent)
                    .map(result -> {
                        try {
                            return (UaParserJsIngestion) JACKSON_OBJECT_READER.readValue(result);
                        } catch (IOException iox) {
                            throw new IllegalArgumentException(String.format("Result %s returned from V8 function call could not be deserialized", result), iox);
                        }
                    });
        } catch (RuntimeException x) {
            throw new UserAgentIngestionException(x);
        }
    }


    public static UserAgentIngredients.Device digestDevice(String device) {
        return Optional.ofNullable(device)
                .map(type -> {
                    switch (type) {
                        case "mobile":
                            return UserAgentIngredients.Device.MOBILE;
                        case "tablet":
                            return UserAgentIngredients.Device.TABLET;
                        case "smarttv":
                            return UserAgentIngredients.Device.SMART_TV;
                        case "wearable":
                            return UserAgentIngredients.Device.WEARABLE;
                        case "console":
                            return UserAgentIngredients.Device.CONSOLE;
                        case "embedded":
                            return UserAgentIngredients.Device.EMBEDDED;
                        default:
                            return UserAgentIngredients.Device.UNKNOWN;
                    }
                }).orElse(UserAgentIngredients.Device.UNKNOWN);
    }

    static Optional<String> executeV8Function(V8 v8, String userAgent) {
        V8Array v8FunctionParams = null;
        try {
            v8.getLocker().acquire();
            v8FunctionParams = new V8Array(v8).push(userAgent);
            return Optional.ofNullable(v8.executeStringFunction(WRAPPER_FUNCTION, v8FunctionParams));
        } finally {
            // Release V8 engine function parameters array
            Optional.ofNullable(v8FunctionParams)
                    .ifPresent(V8Array::release);
            // Release V8 engine lock
            Optional.of(v8.getLocker())
                    .filter(V8Locker::hasLock)
                    .ifPresent(V8Locker::release);
        }
    }
}