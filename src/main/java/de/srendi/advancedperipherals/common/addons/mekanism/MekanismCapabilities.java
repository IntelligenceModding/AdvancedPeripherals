package de.srendi.advancedperipherals.common.addons.mekanism;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class MekanismCapabilities {

    public static final Capability<IGasHandler> GAS_HANDLER = get(new CapabilityToken<>() {
    });


}
