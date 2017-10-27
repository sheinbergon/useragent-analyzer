package org.sheinbergon.useragent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.AsyncAnalyzer;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.cache.AsyncCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_USER_AGENT;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_USER_AGENT_INGREDIENTS;

@RunWith(MockitoJUnitRunner.class)
public class AsyncUserAgentAnalyzerTest {

    @Mock
    private AsyncAnalyzer analyzer;

    @Mock
    private AsyncCache cache;

    private AsyncUserAgentAnalyzer userAgentAnalyzer;

    @Before
    public void setup() {
        userAgentAnalyzer = AsyncUserAgentAnalyzer.builder()
                .analyzer(analyzer)
                .cache(cache)
                .build();
    }

    @Test
    public void cacheHit() {
        mockCacheHit();
        userAgentAnalyzer.process(VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS));
    }

    @Test(expected = UserAgentDigestionException.class)
    public void cacheMissAnalyzerError() throws Throwable {
        try {
            mockCacheMiss();
            mockAnalyzerError();
            userAgentAnalyzer.process(VALID_USER_AGENT).get();
        } catch (ExecutionException x) {
            throw ExceptionUtils.getRootCause(x);
        }
    }

    @Test
    public void cacheMissAnalyzerSuccess() {
        AtomicBoolean cacheWriteFlag = new AtomicBoolean(false);
        mockCacheMiss();
        mockAnalyzerSuccess();
        mockCacheWrite(cacheWriteFlag);
        userAgentAnalyzer.process(VALID_USER_AGENT)
                .thenAccept(ingredients -> {
                    assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS);
                    assertTrue(cacheWriteFlag.get());
                });
    }

    private void mockCacheHit() {
        when(cache.read(VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(Optional.of(VALID_USER_AGENT_INGREDIENTS)));
    }

    private void mockCacheMiss() {
        when(cache.read(VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
    }

    private void mockAnalyzerError() {
        when(analyzer.analyze(VALID_USER_AGENT)).thenThrow(new UserAgentDigestionException("Mock Error"));
    }

    private void mockAnalyzerSuccess() {
        when(analyzer.analyze(VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(VALID_USER_AGENT_INGREDIENTS));
    }

    private void mockCacheWrite(final AtomicBoolean flag) {
        Mockito.when(cache.write(anyString(), any(UserAgentIngredients.class)))
                .thenAnswer(invocation -> {
                    flag.set(true);
                    return null;
                });
    }
}
