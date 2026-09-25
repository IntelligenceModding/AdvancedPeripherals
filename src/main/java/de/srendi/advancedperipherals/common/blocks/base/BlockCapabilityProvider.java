package de.srendi.advancedperipherals.common.blocks.base;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public interface BlockCapabilityProvider {
    void registerCapabilities(RegisterCapabilitiesEvent event);
}
