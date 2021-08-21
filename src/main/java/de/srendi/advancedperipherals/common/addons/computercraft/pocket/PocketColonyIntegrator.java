package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.ResourceLocation;

public class PocketColonyIntegrator extends BasePocket<ColonyPeripheral> {

    public PocketColonyIntegrator() {
        super(new ResourceLocation("advancedperipherals", "colony_pocket"), "pocket.advancedperipherals.colony_pocket", Blocks.COLONY_INTEGRATOR);
    }

    @Override
    protected ColonyPeripheral getPeripheral(IPocketAccess access) {
        return new ColonyPeripheral(access);
    }
}
