package org.sheinbergon.useragent.analyzer.processor.util;

import org.sheinbergon.useragent.analyzer.UserAgentIngredients;

import java.util.Optional;

public class WootheeUtils {

    public static UserAgentIngredients.Device digestDevice(String device) {
        return Optional.ofNullable(device)
                .map(type -> {
                    switch (type) {
                        case "pc":
                            return UserAgentIngredients.Device.PC;
                        case "smartphone":
                        case "mobilephone":
                            return UserAgentIngredients.Device.MOBILE;
                        case "appliance":
                            return UserAgentIngredients.Device.SET_TOP_BOX;
                        case "crawler":
                            return UserAgentIngredients.Device.ROBOT;
                        case "misc":
                            return UserAgentIngredients.Device.SOFTWARE;
                        case "unknown":
                            return UserAgentIngredients.Device.UNKNOWN;
                        default:
                            return UserAgentIngredients.Device.UNKNOWN;
                    }
                }).orElse(UserAgentIngredients.Device.UNKNOWN);
    }
}
