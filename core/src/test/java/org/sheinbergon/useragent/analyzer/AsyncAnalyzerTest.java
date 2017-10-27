package org.sheinbergon.useragent.analyzer;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class AsyncAnalyzerTest {

    @Mock
    private AsyncAnalyzer<Object> analyzer;

    @Test(expected = UserAgentIngestionException.class)
    public void emptyIngestionAnalysis() throws Throwable {
        try {
            mockEmptyIngetsion();
            mockExternalAccess();
            analyzer.analyze(randomString())
                    .get();
        } catch (ExecutionException x) {
            throw ExceptionUtils.getRootCause(x);
        }
    }

    @Test
    public void successfulAnalysis() throws ExecutionException, TimeoutException, InterruptedException {
        mockAnyIngetsion();
        mockSuccesfulDigetsion();
        mockExternalAccess();
        analyzer.analyze(VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS))
                .get();
    }

    private void mockExternalAccess() {
        when(analyzer.analyze(anyString())).thenCallRealMethod();
    }

    private void mockEmptyIngetsion() {
        when(analyzer.ingest(anyString())).thenReturn(CompletableFuture.supplyAsync(Optional::empty));
    }

    private void mockAnyIngetsion() {
        when(analyzer.ingest(anyString()))
                .thenReturn(CompletableFuture.supplyAsync(() -> Optional.of(new Object())));
    }

    private void mockSuccesfulDigetsion() {
        when(analyzer.digest(any())).thenReturn(VALID_USER_AGENT_INGREDIENTS);
    }
}
