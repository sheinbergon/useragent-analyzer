package org.sheinbergon.useragent.analyzer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.cache.AsyncCache;
import org.sheinbergon.useragent.analyzer.exception.UserAgentAnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.processor.AsyncProcessor;
import org.sheinbergon.useragent.analyzer.processor.ProcessorTestUtils;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AsyncUserAgentAnalyzerTest {

    @Mock
    private AsyncProcessor processor;

    @Mock
    private AsyncCache cache;

    private AsyncUserAgentAnalyzer userAgentAnalyzer;

    @Before
    public void setup() {
        userAgentAnalyzer = AsyncUserAgentAnalyzer.builder()
                .processor(processor)
                .cache(cache)
                .build();
    }

    @Test
    public void cacheHit() throws InterruptedException, ExecutionException {
        mockCacheHit();
        userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS))
                .get();
    }

    @Test(expected = UserAgentDigestionException.class)
    public void cacheMissProcessorError() throws Throwable {
        try {
            mockCacheMiss();
            mockProcessorError();
            userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT).get();
        } catch (ExecutionException x) {
            throw ExceptionUtils.getRootCause(x);
        }
    }

    @Test
    public void cacheMissProcessorSuccess() throws InterruptedException, ExecutionException {
        AtomicBoolean cacheWriteFlag = new AtomicBoolean(false);
        mockCacheMiss();
        mockProcessorSuccess();
        mockCacheWrite(cacheWriteFlag);
        userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT)
                .thenAccept(ingredients -> {
                    assertEquals(ingredients, ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS);
                    assertTrue(cacheWriteFlag.get());
                }).get();
    }

    @Test(expected = UserAgentAnalyzerBuildException.class)
    public void buildWithoutProcessor() {
        AsyncUserAgentAnalyzer.builder().build();
    }

    @Test
    public void buildWithoutCache() {
        AsyncUserAgentAnalyzer analyzer = AsyncUserAgentAnalyzer.builder().processor(processor).build();
        assertNotNull(analyzer.cache);
    }

    private void mockCacheHit() {
        when(cache.read(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(Optional.of(ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS)));
    }

    private void mockCacheMiss() {
        when(cache.read(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
    }

    private void mockProcessorError() {
        when(processor.process(ProcessorTestUtils.VALID_USER_AGENT)).thenThrow(new UserAgentDigestionException("Mock Error"));
    }

    private void mockProcessorSuccess() {
        when(processor.process(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(CompletableFuture.completedFuture(ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS));
    }

    private void mockCacheWrite(final AtomicBoolean flag) {
        doAnswer(invocation -> {
            flag.set(true);
            return null;
        }).when(cache).write(anyString(), any(UserAgentIngredients.class));
    }
}
