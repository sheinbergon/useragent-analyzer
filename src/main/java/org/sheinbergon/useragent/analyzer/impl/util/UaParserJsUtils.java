package org.sheinbergon.useragent.analyzer.impl.util;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Locker;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.sheinbergon.useragent.Ingredients;
import org.sheinbergon.useragent.IngredientsDeviceType;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.impl.UaParserJsIngestion;

import java.io.IOException;
import java.util.Optional;

public class UaParserJsUtils {

    private final static String UA_PARSER_JS = "ua-parser.js";
    private final static String WRAPPER = "ua-parser-j2v8-wrapper.js";
    private final static String[] SCRIPTS = new String[]{UA_PARSER_JS, WRAPPER};

    private final static String J2V8_UA_PARSER_WRAPPER_FUNCTION = "UAParserWrapper";
    private final static ObjectReader JACKSON_OBJECT_READER = new ObjectMapper().
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).
            reader().
            forType(UaParserJsIngestion.class);

    public static Ingredients toIngredients(UaParserJsIngestion ingestion) throws UserAgentDigestionException {
        Ingredients.IngredientsBuilder builder = Ingredients.builder();

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
                        .deviceType(toIngredientsDeviceType(device.getType())));

        Optional.ofNullable(ingestion.getCpu())
                .ifPresent(cpu -> builder
                        .cpuArchitecture(cpu.getArchitecture()));

        return builder.build();
    }

    public static UaParserJsIngestion jsonDeserialize(String json) {
        try {
            return JACKSON_OBJECT_READER.readValue(json);
        } catch (IOException iox) {
            throw new UserAgentIngestionException(iox);
        }
    }


    public static Optional<String> v8ScriptExecute(V8 v8, String userAgent) {
        V8Array v8FunctionParams = null;
        try {
            v8.getLocker().acquire();
            v8FunctionParams = new V8Array(v8).push(userAgent);
            return Optional.ofNullable(v8.executeStringFunction(J2V8_UA_PARSER_WRAPPER_FUNCTION, v8FunctionParams));
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


    public static String[] scriptPaths() {
        return SCRIPTS;
    }


    public static IngredientsDeviceType toIngredientsDeviceType(String device) {
        return Optional.ofNullable(device)
                .map(type -> {
                    switch (type) {
                        case "mobile":
                            return IngredientsDeviceType.MOBILE;
                        case "tablet":
                            return IngredientsDeviceType.TABLET;
                        case "smarttv":
                            return IngredientsDeviceType.SMART_TV;
                        case "wearable":
                            return IngredientsDeviceType.WEARABLE;
                        case "console":
                            return IngredientsDeviceType.CONSOLE;
                        case "embedded":
                            return IngredientsDeviceType.EMBEDDED;
                        default:
                            return IngredientsDeviceType.UNKNOWN;
                    }
                }).orElse(IngredientsDeviceType.UNKNOWN);
    }
}
