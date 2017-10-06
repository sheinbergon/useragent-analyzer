package org.sheinbergon.useragent.cache;

import org.sheinbergon.useragent.UserAgentIngredients;

final class TestUtils {

    final static long EVICTION_DELAY = 100L;

    final static int CACHE_SIZE = 1;

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
