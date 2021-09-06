package de.srendi.advancedperipherals.lib.peripherals.owner;

import java.util.Map;

public interface IOwnerAbility {
    default void collectConfiguration(Map<String, Object> dict) {
    }
}
