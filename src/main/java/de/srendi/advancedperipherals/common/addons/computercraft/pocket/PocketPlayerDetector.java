package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketPlayerDetector extends BasePocket<PlayerDetectorPeripheral> {


    public PocketPlayerDetector() {
        super(new ResourceLocation("advancedperipherals", "player_pocket"), "pocket.advancedperipherals.player_pocket", Blocks.PLAYER_DETECTOR);
    }

    @Nullable
    @Override
    public PlayerDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new PlayerDetectorPeripheral(iPocketAccess);
    }

}
