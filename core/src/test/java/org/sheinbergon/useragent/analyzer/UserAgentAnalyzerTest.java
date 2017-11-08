package org.sheinbergon.useragent.analyzer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.cache.Cache;
import org.sheinbergon.useragent.analyzer.cache.PseudoCache;
import org.sheinbergon.useragent.analyzer.exception.UserAgentAnalyzerBuildException;
import org.sheinbergon.useragent.analyzer.processor.Processor;
import org.sheinbergon.useragent.analyzer.processor.ProcessorTestUtils;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAgentAnalyzerTest {

    @Mock
    private Processor processor;

    @Mock
    private Cache cache;

    private UserAgentAnalyzer userAgentAnalyzer;

    @Before
    public void setup() {
        userAgentAnalyzer = UserAgentAnalyzer.builder()
                .processor(processor)
                .cache(cache)
                .build();
    }

    @Test
    public void cacheHit() {
        mockCacheHit();
        UserAgentIngredients ingredients = userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT);
        assertEquals(ingredients, ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS);
    }

    @Test(expected = UserAgentDigestionException.class)
    public void cacheMissProcessorError() {
        mockCacheMiss();
        mockProcessorError();
        userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT);
    }

    @Test
    public void cacheMissProcessorSuccess() {
        AtomicBoolean cacheWriteFlag = new AtomicBoolean(false);
        mockCacheMiss();
        mockProcessorSuccess();
        mockCacheWrite(cacheWriteFlag);
        UserAgentIngredients ingredients = userAgentAnalyzer.analyze(ProcessorTestUtils.VALID_USER_AGENT);
        assertEquals(ingredients, ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS);
        assertTrue(cacheWriteFlag.get());
    }

    @Test(expected = UserAgentAnalyzerBuildException.class)
    public void buildWithoutProcessor() {
        UserAgentAnalyzer.builder().build();
    }

    @Test
    public void buildWithoutCache() {
        UserAgentAnalyzer analyzer = UserAgentAnalyzer.builder().processor(processor).build();
        assertTrue(analyzer.cache instanceof PseudoCache);
    }

    private void mockCacheHit() {
        when(cache.read(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(Optional.of(ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS));
    }

    private void mockCacheMiss() {
        when(cache.read(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(Optional.empty());
    }

    private void mockProcessorError() {
        when(processor.process(ProcessorTestUtils.VALID_USER_AGENT)).thenThrow(new UserAgentDigestionException("Mock Error"));
    }

    private void mockProcessorSuccess() {
        when(processor.process(ProcessorTestUtils.VALID_USER_AGENT)).thenReturn(ProcessorTestUtils.VALID_USER_AGENT_INGREDIENTS);
    }

    private void mockCacheWrite(final AtomicBoolean flag) {
        doAnswer(invocation -> {
            flag.set(true);
            return null;
        }).when(cache).write(anyString(), any(UserAgentIngredients.class));
    }
}
