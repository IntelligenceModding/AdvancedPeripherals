package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketColonyIntegratorUpgrade extends BasePocketUpgrade<ColonyPeripheral> {

    public PocketColonyIntegratorUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Pocket.COLONY, stack);
    }

    @Override
    @NotNull
    protected ColonyPeripheral buildPeripheral(@NotNull IPocketAccess access) {
        return new ColonyPeripheral(access);
    }
}
