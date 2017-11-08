package org.sheinbergon.useragent.analyzer.cache;

import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

public final class CacheTestUtils {

    final static String TEST_KEY_1 = "key-1";
    final static UserAgentIngredients TEST_INGREDIENTS_1 = UserAgentIngredients.builder().
            browserName("firefox").
            deviceModel("cutting-edge").
            osName("ubuntu").
            build();

    final static String TEST_KEY_2 = "key-2";
    final static UserAgentIngredients TEST_INGREDIENTS_2 = UserAgentIngredients.builder().
            browserName("chrome").
            deviceModel("shiny").
            osName("android").
            build();
}
