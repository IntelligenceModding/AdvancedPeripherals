package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketInventoryManagerUpgrade extends BasePocketUpgrade<InventoryManagerPeripheral> {

    public PocketInventoryManagerUpgrade(ItemStack stack) {
        super(CCRegistration.ID.INVENTORY_MANAGER_POCKET, stack);
    }

    @Override
    @NotNull
    protected InventoryManagerPeripheral buildPeripheral(@NotNull IPocketAccess pocketAccess) {
        return new InventoryManagerPeripheral(pocketAccess);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.INVENTORY_MANAGER_POCKET.get();
    }
}
