package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketEnvironmentUpgrade extends BasePocketUpgrade<EnvironmentDetectorPeripheral> {

    public PocketEnvironmentUpgrade(ItemStack stack) {
        super(CCRegistration.ID.ENVIRONMENT_POCKET, stack);
    }

    @Nullable
    @Override
    public EnvironmentDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new EnvironmentDetectorPeripheral(iPocketAccess);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.ENVIRONMENT_POCKET.get();
    }
}
