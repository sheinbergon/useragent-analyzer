package org.sheinbergon.useragent.cache;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.sheinbergon.useragent.cache.CacheTestUtils.TEST_INGREDIENTS_1;
import static org.sheinbergon.useragent.cache.CacheTestUtils.TEST_KEY_1;

@RunWith(JUnit4.class)
public class PseudoCacheTest {

    @Before
    public void setup() {
        cache = PseudoCache.builder()
                .build();
    }

    private PseudoCache cache = null;

    @Test
    public void cacheInsertion() {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1);
        assertFalse(cache.read(TEST_KEY_1).isPresent());
    }
}
