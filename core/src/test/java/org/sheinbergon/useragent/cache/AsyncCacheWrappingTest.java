package org.sheinbergon.useragent.cache;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.cache.CacheTestUtils.TEST_INGREDIENTS_1;
import static org.sheinbergon.useragent.cache.CacheTestUtils.TEST_KEY_1;

@RunWith(MockitoJUnitRunner.class)
public class AsyncCacheWrappingTest {

    @Mock
    private Cache cache;

    private AsyncCache asyncCache;

    @Before
    public void setup() {
        asyncCache = AsyncCache.Builder.wrap(cache);
    }

    @Test
    public void asyncCacheWrapping() throws InterruptedException, ExecutionException, TimeoutException {
        mockCacheReadAccess();
        asyncCache.read(TEST_KEY_1).thenAccept(optional -> {
            assertTrue(optional.isPresent());
            assertEquals(optional.get(), TEST_INGREDIENTS_1);
        }).get();
    }

    private void mockCacheReadAccess() {
        when(cache.read(anyString())).thenReturn(Optional.of(TEST_INGREDIENTS_1));
    }
}
