package de.srendi.advancedperipherals.common.addons.mekanism;

import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class MekanismCapabilities {

    public static final Capability<IGasHandler> GAS_HANDLER = get(new CapabilityToken<>() {
    });
    public static final Capability<ISlurryHandler> SLURRY_HANDLER = get(new CapabilityToken<>() {
    });
    public static final Capability<IPigmentHandler> PIGMENT_HANDLER = get(new CapabilityToken<>() {
    });
    public static final Capability<IInfusionHandler> INFUSION_HANDLER = get(new CapabilityToken<>() {
    });


}
