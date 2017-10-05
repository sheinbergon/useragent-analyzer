package org.sheinbergon.useragent;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Idan Sheinberg
 */
@Builder(builderClassName = "Builder")
@EqualsAndHashCode
@ToString
public class Ingredients {

    @Getter
    private String osName;
    @Getter
    private String osVersion;
    @Getter
    private String browserName;
    @Getter
    private String browserVersion;
    @Getter
    private String renderingEngineName;
    @Getter
    private String renderingEngineVersion;
    @Getter
    private IngredientsDeviceType deviceType;
    @Getter
    private String deviceModel;
    @Getter
    private String deviceMake;
    @Getter
    private String cpuArchitecture;
}
