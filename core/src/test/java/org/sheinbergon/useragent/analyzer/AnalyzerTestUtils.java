package org.sheinbergon.useragent.analyzer;

import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.UUID;

public class AnalyzerTestUtils {

    final static String VALID_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.1.1; ONEPLUS A5000 Build/NMF26X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Mobile Safari/537.36";

    final static UserAgentIngredients VALID_DIGESTION = UserAgentIngredients.builder()
            .browserName("Chrome")
            .browserVersion("56.0.2924.87")
            .deviceType(UserAgentIngredients.Device.MOBILE)
            .osName("Android")
            .osVersion("7.1.1")
            .renderingEngineName("WebKit")
            .renderingEngineVersion("537.36")
            .build();


    static String randomString() {
        return UUID.randomUUID().toString();
    }
}