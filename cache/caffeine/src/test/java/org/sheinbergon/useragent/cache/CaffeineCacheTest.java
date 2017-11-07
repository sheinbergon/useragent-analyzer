package org.sheinbergon.useragent.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sheinbergon.useragent.UserAgentIngredients;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.sheinbergon.useragent.cache.CacheTestUtils.*;
import static org.sheinbergon.useragent.cache.CaffeineTestUtils.CACHE_SIZE;
import static org.sheinbergon.useragent.cache.CaffeineTestUtils.EVICTION_DELAY;

public class CaffeineCacheTest {

    @Before
    public void setup() {
        cache = CaffeineCache.builder()
                .maxEntries(CACHE_SIZE)
                .build();
    }

    @After
    public void teardown() {
        cache.teardown();
    }

    private CaffeineCache cache = null;

    @Test
    public void cacheInsertion() {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1);
        Optional<UserAgentIngredients> optional = cache.read(TEST_KEY_1);
        assertTrue(optional.isPresent());
        assertEquals(optional.get(), TEST_INGREDIENTS_1);
    }

    @Test
    public void cacheEviction() throws InterruptedException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1);
        cache.write(TEST_KEY_2, TEST_INGREDIENTS_2);
        Thread.sleep(EVICTION_DELAY);
        assertFalse(cache.read(TEST_KEY_1).isPresent());
    }
}
