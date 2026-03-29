package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketDistanceDetectorUpgrade extends BasePocketUpgrade<DistanceDetectorPeripheral> {

    public PocketDistanceDetectorUpgrade(ItemStack stack) {
        super(CCRegistration.ID.DISTANCE_POCKET, stack);
    }

    @Override
    @NotNull
    protected DistanceDetectorPeripheral buildPeripheral(@NotNull IPocketAccess pocketAccess) {
        return new DistanceDetectorPeripheral(pocketAccess);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.DISTANCE_DETECTOR_POCKET.get();
    }
}
