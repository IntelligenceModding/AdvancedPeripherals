package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RedstoneIntegratorTile extends PeripheralTileEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.REDSTONE_INTEGRATOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral("redstoneIntegrator", this);
    }

    public int getRedstoneInput(Direction direction) {
        BlockPos neighbourPos = getBlockPos().relative(direction);
        int power = getLevel().getSignal(neighbourPos, direction);
        if (power >= 15) return power;

        BlockState neighbourState = getLevel().getBlockState(neighbourPos);
        return neighbourState.getBlock() == Blocks.REDSTONE_WIRE
                ? Math.max(power, neighbourState.getValue(RedStoneWireBlock.POWER)) : power;
    }

    public void setRedstoneOutput(Direction direction, int power) {
        int old = this.power[direction.get3DDataValue()];
        this.power[direction.get3DDataValue()] = power;
        if (old != power) {
            if (level != null)
                level.updateNeighborsAt(getBlockPos().relative(direction),
                        getLevel().getBlockState(getBlockPos().relative(direction)).getBlock());

            this.setChanged();
        }
    }

    @Override
    public void load(CompoundTag compound) {
        for (Direction direction : Direction.values()) {
            setRedstoneOutput(direction, compound.getInt(direction.name() + "Power"));
        }
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        int i = 0;
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.name() + "Power", power[i]);
            i++;
        }
        return compound;
    }

}
