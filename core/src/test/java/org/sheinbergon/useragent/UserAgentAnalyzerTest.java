package org.sheinbergon.useragent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.Analyzer;
import org.sheinbergon.useragent.analyzer.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.cache.Cache;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_USER_AGENT;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.VALID_USER_AGENT_INGREDIENTS;

@RunWith(MockitoJUnitRunner.class)
public class UserAgentAnalyzerTest {

    @Mock
    private Analyzer analyzer;

    @Mock
    private Cache cache;

    private UserAgentAnalyzer userAgentAnalyzer;

    @Before
    public void setup() {
        userAgentAnalyzer = UserAgentAnalyzer.builder()
                .analyzer(analyzer)
                .cache(cache)
                .build();
    }

    @Test
    public void cacheHit() {
        mockCacheHit();
        UserAgentIngredients ingredients = userAgentAnalyzer.process(VALID_USER_AGENT);
        assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS);
    }

    @Test(expected = UserAgentDigestionException.class)
    public void cacheMissAnalyzerError() {
        mockCacheMiss();
        mockAnalyzerError();
        userAgentAnalyzer.process(VALID_USER_AGENT);
    }

    @Test
    public void cacheMissAnalyzerSuccess() {
        AtomicBoolean cacheWriteFlag = new AtomicBoolean(false);
        mockCacheMiss();
        mockAnalyzerSuccess();
        mockCacheWrite(cacheWriteFlag);
        UserAgentIngredients ingredients = userAgentAnalyzer.process(VALID_USER_AGENT);
        assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS);
        assertTrue(cacheWriteFlag.get());
    }

    private void mockCacheHit() {
        when(cache.read(VALID_USER_AGENT)).thenReturn(Optional.of(VALID_USER_AGENT_INGREDIENTS));
    }

    private void mockCacheMiss() {
        when(cache.read(VALID_USER_AGENT)).thenReturn(Optional.empty());
    }

    private void mockAnalyzerError() {
        when(analyzer.analyze(VALID_USER_AGENT)).thenThrow(new UserAgentDigestionException("Mock Error"));
    }

    private void mockAnalyzerSuccess() {
        when(analyzer.analyze(VALID_USER_AGENT)).thenReturn(VALID_USER_AGENT_INGREDIENTS);
    }

    private void mockCacheWrite(final AtomicBoolean flag) {
        doAnswer(invocation -> {
            flag.set(true);
            return null;
        }).when(cache).write(anyString(), any(UserAgentIngredients.class));
    }
}
