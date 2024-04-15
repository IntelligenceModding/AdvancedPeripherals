package de.srendi.advancedperipherals.lib.misc;

import net.neoforged.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface IConfigHandler {
    void addToConfig(ForgeConfigSpec.Builder builder);

    String name();

    default String settingsPostfix() {
        return "";
    }

    default String settingsName() {
        String name = name();
        String startName = Arrays.stream(name.toLowerCase().split("_")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining()) + settingsPostfix();
        return startName.substring(0, 1).toLowerCase() + startName.substring(1);
    }
}
