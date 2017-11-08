package org.sheinbergon.useragent.analyzer.cache;

import org.jooq.lambda.Unchecked;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.sheinbergon.useragent.analyzer.cache.CacheTestUtils.*;

public class CaffeineAsyncCacheTest {

    @Before
    public void setup() {
        cache = CaffeineAsyncCache.builder()
                .maxEntries(CaffeineTestUtils.CACHE_SIZE)
                .build();
    }

    private CaffeineAsyncCache cache = null;

    @Test
    public void cacheInsertion() throws ExecutionException, TimeoutException, InterruptedException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1)
                .thenCompose(unit -> cache.read(TEST_KEY_1))
                .thenAccept(optional -> {
                    assertTrue(optional.isPresent());
                    assertEquals(optional.get(), TEST_INGREDIENTS_1);
                })
                .get();
    }

    @Test
    public void cacheEviction() throws ExecutionException, TimeoutException, InterruptedException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1)
                .thenCompose(unit -> cache.write(TEST_KEY_2, TEST_INGREDIENTS_2))
                .thenRun(Unchecked.runnable(() -> Thread.sleep(CaffeineTestUtils.EVICTION_DELAY)))
                .thenCompose(unit -> cache.read(TEST_KEY_1))
                .thenAccept(optional -> assertFalse(optional.isPresent()))
                .get();

    }
}
