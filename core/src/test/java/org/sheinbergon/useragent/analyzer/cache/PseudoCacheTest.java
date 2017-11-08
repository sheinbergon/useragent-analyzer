package org.sheinbergon.useragent.analyzer.cache;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;

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
        cache.write(CacheTestUtils.TEST_KEY_1, CacheTestUtils.TEST_INGREDIENTS_1);
        assertFalse(cache.read(CacheTestUtils.TEST_KEY_1).isPresent());
    }
}
