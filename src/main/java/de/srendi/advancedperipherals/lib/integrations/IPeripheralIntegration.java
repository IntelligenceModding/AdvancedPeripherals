package de.srendi.advancedperipherals.lib.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface IPeripheralIntegration {
    boolean isSuitable(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction);

    @NotNull IPeripheral buildPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction);

    /**
     * @return integration priority, lower priority is better
     */
    int getPriority();
}
