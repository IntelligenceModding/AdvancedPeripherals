package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketEnvironmentUpgrade extends BasePocketUpgrade<EnvironmentDetectorPeripheral> {

    public PocketEnvironmentUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Pocket.ENVIRONMENT, stack);
    }

    @Override
    @NotNull
    protected EnvironmentDetectorPeripheral buildPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new EnvironmentDetectorPeripheral(iPocketAccess);
    }
}
