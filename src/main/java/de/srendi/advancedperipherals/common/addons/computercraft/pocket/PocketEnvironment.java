package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketEnvironment extends BasePocket<EnvironmentDetectorPeripheral> {

    public PocketEnvironment() {
        super(new ResourceLocation("advancedperipherals", "environment_pocket"), "pocket.advancedperipherals.environment_pocket", Blocks.ENVIRONMENT_DETECTOR);
    }

    @Nullable
    @Override
    public EnvironmentDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new EnvironmentDetectorPeripheral("environmentDetector", iPocketAccess);
    }

}
