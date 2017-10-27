package org.sheinbergon.useragent.analyzer;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sheinbergon.useragent.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.exception.UserAgentIngestionException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sheinbergon.useragent.analyzer.AnalyzerTestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class AnalyzerTest {

    @Mock
    private Analyzer<Object> analyzer;

    @Test(expected = UserAgentIngestionException.class)
    public void emptyIngestionAnalysis() {
        mockEmptyIngetsion();
        mockExternalAccess();
        analyzer.analyze(randomString());
    }

    @Test
    public void successfulAnalysis() {
        mockAnyIngetsion();
        mockSuccesfulDigetsion();
        mockExternalAccess();
        UserAgentIngredients ingredients = analyzer.analyze(VALID_USER_AGENT);
        assertEquals(ingredients, VALID_DIGESTION);
    }

    private void mockExternalAccess() {
        when(analyzer.analyze(anyString())).thenCallRealMethod();
    }

    private void mockEmptyIngetsion() {
        when(analyzer.ingest(anyString())).thenReturn(Optional.empty());
    }

    private void mockAnyIngetsion() {
        when(analyzer.ingest(anyString()))
                .thenReturn(Optional.of(new Object()));
    }

    private void mockSuccesfulDigetsion() {
        when(analyzer.digest(any())).thenReturn(VALID_DIGESTION);
    }
}
