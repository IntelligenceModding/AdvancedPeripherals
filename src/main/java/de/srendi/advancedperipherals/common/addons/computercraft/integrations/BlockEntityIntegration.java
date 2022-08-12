package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class BlockEntityIntegration implements IPeripheralIntegration {

    private static final int DEFAULT_PRIORITY = 50;

    private final Function<BlockEntity, ? extends IPeripheral> build;
    private final Predicate<BlockEntity> predicate;
    private final int priority;

    public BlockEntityIntegration(Function<BlockEntity, ? extends IPeripheral> build, Predicate<BlockEntity> predicate, int priority) {
        this.build = build;
        this.predicate = predicate;
        this.priority = priority;
    }

    public BlockEntityIntegration(Function<BlockEntity, ? extends IPeripheral> build, Predicate<BlockEntity> predicate) {
        this(build, predicate, DEFAULT_PRIORITY);
    }

    @Override
    public boolean isSuitable(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity te = level.getBlockEntity(blockPos);
        if (te == null) return false;
        return predicate.test(te);
    }

    @Override
    public @NotNull IPeripheral buildPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity te = level.getBlockEntity(blockPos);
        if (te == null) throw new IllegalArgumentException("This should not happen");
        return build.apply(te);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
