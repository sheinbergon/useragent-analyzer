package org.sheinbergon.useragent.processor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.processor.exception.UserAgentIngestionException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.processor.ProcessorTestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    @Mock
    private Processor<Object> processor;

    @Test(expected = UserAgentIngestionException.class)
    public void emptyIngestionProcessing() {
        mockEmptyIngetsion();
        mockExternalAccess();
        processor.process(randomString());
    }

    @Test
    public void successfulProcessing() {
        mockAnyIngetsion();
        mockSuccesfulDigetsion();
        mockExternalAccess();
        UserAgentIngredients ingredients = processor.process(VALID_USER_AGENT);
        assertEquals(ingredients, VALID_USER_AGENT_INGREDIENTS);
    }

    private void mockExternalAccess() {
        when(processor.process(anyString())).thenCallRealMethod();
    }

    private void mockEmptyIngetsion() {
        when(processor.ingest(anyString())).thenReturn(Optional.empty());
    }

    private void mockAnyIngetsion() {
        when(processor.ingest(anyString()))
                .thenReturn(Optional.of(new Object()));
    }

    private void mockSuccesfulDigetsion() {
        when(processor.digest(any())).thenReturn(VALID_USER_AGENT_INGREDIENTS);
    }
}
