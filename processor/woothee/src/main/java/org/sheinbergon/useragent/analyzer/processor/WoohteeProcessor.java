package org.sheinbergon.useragent.analyzer.processor;

import is.tagomor.woothee.Classifier;
import lombok.Builder;
import org.sheinbergon.useragent.analyzer.UserAgentIngredients;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentDigestionException;
import org.sheinbergon.useragent.analyzer.processor.exception.UserAgentIngestionException;
import org.sheinbergon.useragent.analyzer.processor.util.WootheeUtils;

import java.util.Map;
import java.util.Optional;


@Builder(builderClassName = "Builder")
public class WoohteeProcessor extends Processor<Map<String, String>> {

    @Override
    public void teardown() {
    }

    @Override
    protected Optional<Map<String, String>> ingest(String userAgent) throws UserAgentIngestionException {
        return Optional.of(Classifier.execParse(userAgent));
    }

    @Override
    protected UserAgentIngredients digest(Map<String, String> ingestion) throws UserAgentDigestionException {
        return UserAgentIngredients.builder()
                .deviceType(WootheeUtils.digestDevice(ingestion.get("category")))
                .browserName(ingestion.get("name"))
                .browserVersion(ingestion.get("version"))
                .osName(ingestion.get("os"))
                .osVersion(ingestion.get("os_version"))
                .build();
    }
}
