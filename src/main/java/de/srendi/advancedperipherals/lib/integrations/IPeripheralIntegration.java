package de.srendi.advancedperipherals.lib.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface IPeripheralIntegration {
    boolean isSuitable(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction);
    @NotNull IPeripheral buildPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction);

    /**
     * @return integration priority, lower priority is better
     */
    int getPriority();
}
