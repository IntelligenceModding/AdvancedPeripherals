package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BlockIntegrationPeripheral<T extends Block> extends IntegrationPeripheral {

    protected final Level world;
    protected final BlockPos pos;

    protected BlockIntegrationPeripheral(Level world, BlockPos pos) {
        super();
        this.world = world;
        this.pos = pos;
    }

    public Block getBlock() {
        return world.getBlockState(pos).getBlock();
    }

    @Nullable
    @Override
    public Object getTarget() {
        return world.getBlockState(pos).getBlock();
    }
}
