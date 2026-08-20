package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketDistanceDetectorUpgrade extends BasePocketUpgrade<DistanceDetectorPeripheral> {

    public PocketDistanceDetectorUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Pocket.DISTANCE, stack);
    }

    @Override
    @NotNull
    protected DistanceDetectorPeripheral buildPeripheral(@NotNull IPocketAccess pocketAccess) {
        return new DistanceDetectorPeripheral(pocketAccess);
    }
}
