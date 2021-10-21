package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BlockIntegrationPeripheral<T extends Block> extends IntegrationPeripheral {

    protected final Level level;
    protected final BlockPos pos;

    public BlockIntegrationPeripheral(Level level, BlockPos pos) {
        super();
        this.level = level;
        this.pos = pos;
    }

    public Block getBlock() {
        return level.getBlockState(pos).getBlock();
    }

    @Nullable
    @Override
    public Object getTarget() {
        return level.getBlockState(pos).getBlock();
    }
}
