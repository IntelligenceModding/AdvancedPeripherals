package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class BlockIntegration implements IPeripheralIntegration {

    private static final int DEFAULT_PRIORITY = 50;

    private final BiFunction<Level, BlockPos, ? extends IPeripheral> build;
    private final Predicate<Block> predicate;
    private final int priority;

    public BlockIntegration(BiFunction<Level, BlockPos, ? extends IPeripheral> build, Predicate<Block> predicate, int priority) {
        this.build = build;
        this.predicate = predicate;
        this.priority = priority;
    }

    public BlockIntegration(BiFunction<Level, BlockPos, ? extends IPeripheral> build, Predicate<Block> predicate) {
        this(build, predicate, DEFAULT_PRIORITY);
    }

    @Override
    public boolean isSuitable(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        Block block = world.getBlockState(blockPos).getBlock();
        return predicate.test(block);
    }

    @Override
    public @NotNull IPeripheral buildPeripheral(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        return build.apply(world, blockPos);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
