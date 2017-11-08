package org.sheinbergon.useragent.analyzer.processor;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentIngestionException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.analyzer.processor.ProcessorTestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class AsyncProcessorTest {

    @Mock
    private AsyncProcessor<Object> processor;

    @Test(expected = UserAgentIngestionException.class)
    public void emptyIngestionProcessing() throws Throwable {
        try {
            mockEmptyIngetsion();
            mockExternalAccess();
            processor.process(randomString())
                    .get();
        } catch (ExecutionException x) {
            throw ExceptionUtils.getRootCause(x);
        }
    }

    @Test
    public void successfulProcessing() throws ExecutionException, TimeoutException, InterruptedException {
        mockAnyIngetsion();
        mockSuccesfulDigetsion();
        mockExternalAccess();
        processor.process(VALID_USER_AGENT)
                .thenAccept(ingredients -> assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS))
                .get();
    }

    private void mockExternalAccess() {
        when(processor.process(anyString())).thenCallRealMethod();
    }

    private void mockEmptyIngetsion() {
        when(processor.ingest(anyString())).thenReturn(CompletableFuture.supplyAsync(Optional::empty));
    }

    private void mockAnyIngetsion() {
        when(processor.ingest(anyString()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(new Object())));
    }

    private void mockSuccesfulDigetsion() {
        when(processor.digest(any())).thenReturn(VALID_USER_AGENT_INGREDIENTS);
    }
}
