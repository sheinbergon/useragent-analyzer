package org.sheinbergon.useragent.analyzer.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WoohteeAsyncProcessor {

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Accessors(chain = true, fluent = true)
    public static class Builder extends AsyncProcessor.Builder<Map<String, String>, AsyncProcessor<Map<String, String>>> {

        @Setter
        private int ingestionConcurrency = 16;

        @Override
        public AsyncProcessor<Map<String, String>> build() {
            return wrap(WoohteeProcessor.builder().build(), ingestionConcurrency);
        }
    }
}