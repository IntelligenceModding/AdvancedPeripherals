package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class RedstoneIntegratorTile extends PeripheralTileEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorTile() {
        super(TileEntityTypes.REDSTONE_INTEGRATOR.get());
    }

    @NotNull
    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral("redstoneIntegrator", this);
    }

    public void updateRedstone(BlockState newState, EnumSet<Direction> updateDirections, boolean doConductedPowerUpdates) {
        Block newBlock = newState.getBlock();
        if (!net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(level, getBlockPos(), newState, updateDirections, false).isCanceled()) {
            for (Direction direction : updateDirections) {
                BlockPos neighborPos = getBlockPos().relative(direction);
                boolean doSecondaryNeighborUpdates = doConductedPowerUpdates && level.getBlockState(neighborPos).shouldCheckWeakPower(level, neighborPos, direction);
                level.neighborChanged(neighborPos, newBlock, getBlockPos());
                if (doSecondaryNeighborUpdates)
                    level.updateNeighborsAt(neighborPos, newBlock);
            }
        }
    }

    public int getRedstoneInput(Direction direction) {
        BlockPos neighbourPos = getBlockPos().relative(direction);
        int power = getLevel().getSignal(neighbourPos, direction);
        if (power >= 15) return power;

        BlockState neighbourState = getLevel().getBlockState(neighbourPos);
        return neighbourState.getBlock() == Blocks.REDSTONE_WIRE
                ? Math.max(power, neighbourState.getValue(RedstoneWireBlock.POWER)) : power;
    }

    public void setRedstoneOutput(Direction direction, int power) {
        int old = this.power[direction.get3DDataValue()];
        this.power[direction.get3DDataValue()] = power;
        if (old != power) {
            if (getLevel().getBlockState(getBlockPos().relative(direction)).hasProperty(RedstoneWireBlock.POWER)) {
                BlockState state = getLevel().getBlockState(getBlockPos().relative(direction)).setValue(RedstoneWireBlock.POWER, power);
                level.setBlockAndUpdate(getBlockPos().relative(direction), state);
                updateRedstone(state, EnumSet.of(direction), true);
            } else if (getLevel().getBlockState(getBlockPos().relative(direction)).hasProperty(RedstoneDiodeBlock.POWERED)) {
                BlockState state = getLevel().getBlockState(getBlockPos().relative(direction)).setValue(RedstoneDiodeBlock.POWERED, power > 0);
                level.setBlockAndUpdate(getBlockPos().relative(direction), state);
                updateRedstone(state, EnumSet.of(direction), true);
            }
            this.setChanged();
        }
    }

    @Override
    public void deserializeNBT(BlockState state, CompoundNBT compound) {
        for (Direction direction : Direction.values()) {
            setRedstoneOutput(direction, compound.getInt(direction.name() + "Power"));
        }
        super.deserializeNBT(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        int i = 0;
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.name() + "Power", power[i]);
            i++;
        }
        return compound;
    }

    public Direction getDirecton(ComputerSide computerSide) {
        Direction output = Direction.DOWN;
        if (computerSide == ComputerSide.FRONT) output = getBlockState().getValue(RedstoneIntegratorBlock.FACING);
        if (computerSide == ComputerSide.BACK)
            output = getBlockState().getValue(RedstoneIntegratorBlock.FACING).getOpposite();
        if (computerSide == ComputerSide.TOP) output = Direction.UP;
        if (computerSide == ComputerSide.BOTTOM) output = Direction.DOWN;
        if (computerSide == ComputerSide.RIGHT)
            output = getBlockState().getValue(RedstoneIntegratorBlock.FACING).getCounterClockWise();
        if (computerSide == ComputerSide.LEFT)
            output = getBlockState().getValue(RedstoneIntegratorBlock.FACING).getClockWise();
        return output;
    }
}
