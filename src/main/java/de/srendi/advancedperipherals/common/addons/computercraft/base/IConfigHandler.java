package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface IConfigHandler {
    void addToConfig(final ForgeConfigSpec.Builder builder);
    String name();
    default String settingsPostfix() {
        return "";
    }
    default String settingsName() {
        String name = name();
        return Arrays.stream(name.toLowerCase().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining()) + settingsPostfix();
    }
}
