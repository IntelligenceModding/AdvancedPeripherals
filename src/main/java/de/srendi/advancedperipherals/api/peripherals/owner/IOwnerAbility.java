package de.srendi.advancedperipherals.api.peripherals.owner;

import java.util.Map;

public interface IOwnerAbility {
    default void collectConfiguration(Map<String, Object> dict) {
    }
}
