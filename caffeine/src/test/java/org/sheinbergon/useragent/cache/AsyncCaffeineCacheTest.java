package org.sheinbergon.useragent.cache;

import org.jooq.lambda.Unchecked;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.sheinbergon.useragent.cache.TestUtils.*;

public class AsyncCaffeineCacheTest {

    private final static long FUTURE_WAIT_TIMEOUT = 50L;

    @Before
    public void setup() {
        cache = AsyncCaffeineCache.builder()
                .maxEntries(CACHE_SIZE)
                .build();
    }

    private AsyncCaffeineCache cache = null;

    @Test
    public void verifyCacheInsertion() throws ExecutionException, TimeoutException, InterruptedException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1)
                .thenCompose(unit -> cache.read(TEST_KEY_1))
                .thenAccept(optional -> {
                    assertTrue(optional.isPresent());
                    assertEquals(optional.get(), TEST_INGREDIENTS_1);
                })
                .get(FUTURE_WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test
    public void verifyCacheEviction() throws ExecutionException, TimeoutException, InterruptedException {
        cache.write(TEST_KEY_1, TEST_INGREDIENTS_1)
                .thenCompose(unit -> cache.write(TEST_KEY_2, TEST_INGREDIENTS_2))
                .thenRun(Unchecked.runnable(() -> Thread.sleep(EVICTION_DELAY)))
                .thenCompose(unit -> cache.read(TEST_KEY_1))
                .thenAccept(optional -> assertFalse(optional.isPresent()))
                .get(FUTURE_WAIT_TIMEOUT, TimeUnit.MILLISECONDS);

    }
}
