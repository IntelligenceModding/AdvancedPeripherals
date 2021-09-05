package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class TileEntityIntegration implements IPeripheralIntegration {

    private final static int DEFAULT_PRIORITY = 50;

    private final Function<TileEntity, ? extends IPeripheral> build;
    private final Predicate<TileEntity> predicate;
    private final int priority;

    public TileEntityIntegration(Function<TileEntity, ? extends IPeripheral> build, Predicate<TileEntity> predicate, int priority) {
        this.build = build;
        this.predicate = predicate;
        this.priority = priority;
    }

    public TileEntityIntegration(Function<TileEntity, ? extends IPeripheral> build, Predicate<TileEntity> predicate) {
        this(build, predicate, DEFAULT_PRIORITY);
    }

    @Override
    public boolean isSuitable(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        TileEntity te = world.getBlockEntity(blockPos);
        if (te == null)
            return false;
        return predicate.test(te);
    }

    @Override
    public @NotNull IPeripheral buildPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        TileEntity te = world.getBlockEntity(blockPos);
        if (te == null)
            throw new IllegalArgumentException("This should happen");
        return build.apply(te);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
