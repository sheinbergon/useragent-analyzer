package org.sheinbergon.useragent.cache;

import org.sheinbergon.useragent.Ingredients;

final class TestUtils {

    final static long EVICTION_DELAY = 50L;

    final static int CACHE_SIZE = 1;


    final static String TEST_KEY_1 = "key-1";
    final static Ingredients TEST_INGREDIENTS_1 = Ingredients.builder().
            browserName("firefox").
            deviceModel("cutting-edge").
            osName("ubuntu").
            build();

    final static String TEST_KEY_2 = "key-2";
    final static Ingredients TEST_INGREDIENTS_2 = Ingredients.builder().
            browserName("chrome").
            deviceModel("shiny").
            osName("android").
            build();
}
