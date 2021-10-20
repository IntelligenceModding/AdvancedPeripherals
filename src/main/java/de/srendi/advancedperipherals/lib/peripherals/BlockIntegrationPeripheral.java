package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockIntegrationPeripheral<T extends Block> extends IntegrationPeripheral {

    protected final World world;
    protected final BlockPos pos;

    public BlockIntegrationPeripheral( World world, BlockPos pos) {
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
