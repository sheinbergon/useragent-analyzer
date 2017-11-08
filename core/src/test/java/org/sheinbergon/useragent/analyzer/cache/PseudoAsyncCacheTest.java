package org.sheinbergon.useragent.analyzer.cache;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.sheinbergon.useragent.analyzer.cache.CacheTestUtils.TEST_INGREDIENTS_1;
import static org.sheinbergon.useragent.analyzer.cache.CacheTestUtils.TEST_KEY_1;

@RunWith(JUnit4.class)
public class PseudoAsyncCacheTest {

    @Before
    public void setup() {
        cache = PseudoAsyncCache.builder()
                .build();
    }

    private AsyncCache cache = null;

    @Test
    public void cacheInsertion() throws ExecutionException, InterruptedException, TimeoutException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1);
        cache.read(TEST_KEY_1).thenAccept(optional -> {
            assertFalse(optional.isPresent());
        }).get();
    }
}
