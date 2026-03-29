package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketPlayerDetectorUpgrade extends BasePocketUpgrade<PlayerDetectorPeripheral> {

    public PocketPlayerDetectorUpgrade(ItemStack stack) {
        super(CCRegistration.ID.PLAYER_POCKET, stack);
    }

    @Override
    @NotNull
    protected PlayerDetectorPeripheral buildPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new PlayerDetectorPeripheral(iPocketAccess);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.PLAYER_DETECTOR_POCKET.get();
    }
}
