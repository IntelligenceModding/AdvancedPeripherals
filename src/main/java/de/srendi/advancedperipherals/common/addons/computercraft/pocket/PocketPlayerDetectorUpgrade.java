package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketPlayerDetectorUpgrade extends BasePocketUpgrade<PlayerDetectorPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "player_pocket");

    public PocketPlayerDetectorUpgrade() {
        super(ID, Blocks.PLAYER_DETECTOR);
    }

    @Nullable
    @Override
    public PlayerDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new PlayerDetectorPeripheral(iPocketAccess);
    }

}
