package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import de.srendi.advancedperipherals.lib.misc.TriFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BlockIntegration implements IPeripheralIntegration {

    private final static int DEFAULT_PRIORITY = 50;

    private final BiFunction<World, BlockPos, ? extends IPeripheral> build;
    private final Predicate<Block> predicate;
    private final int priority;

    public BlockIntegration(BiFunction<World, BlockPos, ? extends IPeripheral> build, Predicate<Block> predicate, int priority) {
        this.build = build;
        this.predicate = predicate;
        this.priority = priority;
    }

    public BlockIntegration(BiFunction<World, BlockPos, ? extends IPeripheral> build, Predicate<Block> predicate) {
        this(build, predicate, DEFAULT_PRIORITY);
    }

    @Override
    public boolean isSuitable(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        Block block = world.getBlockState(blockPos).getBlock();
        return predicate.test(block);
    }

    @Override
    public @NotNull IPeripheral buildPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        return build.apply(world, blockPos);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
