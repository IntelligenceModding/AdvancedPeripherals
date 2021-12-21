package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PocketColonyIntegratorUpgrade extends BasePocketUpgrade<ColonyPeripheral> {

    public PocketColonyIntegratorUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Override
    protected ColonyPeripheral getPeripheral(IPocketAccess access) {
        return new ColonyPeripheral(access);
    }
}
