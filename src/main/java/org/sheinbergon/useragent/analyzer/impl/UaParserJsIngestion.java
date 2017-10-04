package org.sheinbergon.useragent.analyzer.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * @author Idan Sheinberg
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UaParserJsIngestion {

    @JsonProperty("ua")
    private String ua;
    @JsonProperty("browser")
    private Browser browser;
    @JsonProperty("engine")
    private Engine engine;
    @JsonProperty("os")
    private Os os;
    @JsonProperty("device")
    private Device device;
    @JsonProperty("cpu")
    private Cpu cpu;

    /**
     * @author Idan Sheinberg
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Browser {

        @JsonProperty(value = "name")
        private String name;
        @JsonProperty(value = "version")
        private String version;
    }

    /**
     * @author idans
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cpu {
        @JsonProperty(value = "architecture")
        private String architecture;
    }

    /**
     * @author idans
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Device {

        @JsonProperty(value = "model")
        private String model;
        @JsonProperty(value = "type")
        private String type;
        @JsonProperty(value = "vendor")
        private String vendor;
    }

    /**
     * @author Idan Sheinberg
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Engine {
        @JsonProperty(value = "name")
        private String name;
        @JsonProperty(value = "version")
        private String version;
    }

    /**
     * @author Idan Sheinberg
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder(value = {"name", "version"})
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Os {

        @JsonProperty(value = "name")
        private String name;
        @JsonProperty(value = "version")
        private String version;
    }
}

