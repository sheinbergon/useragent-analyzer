package org.sheinbergon.useragent.analyzer;

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
public class UserAgentIngredients {

    public static final UserAgentIngredients EMPTY = builder().deviceType(Device.UNKNOWN).build();

    public enum Device {
        UNKNOWN,
        PC,
        ROBOT,
        SOFTWARE,
        SET_TOP_BOX,
        MOBILE,
        TABLET,
        SMART_TV,
        WEARABLE,
        CONSOLE,
        EMBEDDED
    }

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
    private Device deviceType;
    @Getter
    private String deviceModel;
    @Getter
    private String deviceMake;
    @Getter
    private String cpuArchitecture;
}


