package de.srendi.advancedperipherals.lib.misc;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public interface IConfigHandler {
    void addToConfig(ModConfigSpec.Builder builder);

    String name();

    default String settingsPostfix() {
        return "";
    }

    default String settingsName() {
        String name = name();
        String startName = Arrays.stream(name.toLowerCase(Locale.ROOT).split("_")).map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT)).collect(Collectors.joining()) + settingsPostfix();
        return startName.substring(0, 1).toLowerCase(Locale.ROOT) + startName.substring(1);
    }
}
