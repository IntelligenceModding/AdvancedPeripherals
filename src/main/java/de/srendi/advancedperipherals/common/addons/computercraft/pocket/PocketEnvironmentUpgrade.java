package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketEnvironmentUpgrade extends BasePocketUpgrade<EnvironmentDetectorPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "environment_pocket");

    public PocketEnvironmentUpgrade() {
        super(ID, Blocks.ENVIRONMENT_DETECTOR);
    }

    @Nullable
    @Override
    public EnvironmentDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new EnvironmentDetectorPeripheral(iPocketAccess);
    }

}
