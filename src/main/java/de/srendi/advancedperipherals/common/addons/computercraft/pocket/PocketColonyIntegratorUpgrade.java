package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;

public class PocketColonyIntegratorUpgrade extends BasePocketUpgrade<ColonyPeripheral> {

    public PocketColonyIntegratorUpgrade(ItemStack stack) {
        super(CCRegistration.ID.COLONY_POCKET, stack);
    }

    @Override
    protected ColonyPeripheral getPeripheral(IPocketAccess access) {
        return new ColonyPeripheral(access);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.COLONY_POCKET.get();
    }
}
