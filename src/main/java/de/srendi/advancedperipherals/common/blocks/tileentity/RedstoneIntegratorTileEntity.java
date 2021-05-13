package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class RedstoneIntegratorTileEntity extends PeripheralTileEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorTileEntity() {
        super(TileEntityTypes.REDSTONE_INTEGRATOR.get());
    }

    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral("redstoneIntegrator", this);
    }

    public void updateRedstone(BlockState newState, EnumSet<Direction> updateDirections, boolean doConductedPowerUpdates) {
        Block newBlock = newState.getBlock();
        if (!net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, getPos(), newState, updateDirections, false).isCanceled()) {
            for (Direction direction : updateDirections) {
                BlockPos neighborPos = getPos().offset(direction);
                boolean doSecondaryNeighborUpdates = doConductedPowerUpdates && world.getBlockState(neighborPos).shouldCheckWeakPower(world, neighborPos, direction);
                world.neighborChanged(neighborPos, newBlock, getPos());
                if (doSecondaryNeighborUpdates)
                    world.notifyNeighborsOfStateChange(neighborPos, newBlock);
            }
        }
    }

    public int getRedstoneInput(Direction direction) {
        BlockPos neighbourPos = getPos().offset(direction);
        int power = getWorld().getRedstonePower(neighbourPos, direction);
        if (power >= 15) return power;

        BlockState neighbourState = getWorld().getBlockState(neighbourPos);
        return neighbourState.getBlock() == Blocks.REDSTONE_WIRE
                ? Math.max(power, neighbourState.get(RedstoneWireBlock.POWER)) : power;
    }

    public void setRedstoneOutput(Direction direction, int power) {
        int old = this.power[direction.getIndex()];
        this.power[direction.getIndex()] = power;
        if (old != power) {
            if (!getWorld().getBlockState(pos.offset(direction)).hasProperty(RedstoneWireBlock.POWER))
                return;
            BlockState state = getWorld().getBlockState(pos.offset(direction)).with(RedstoneWireBlock.POWER, power);
            world.setBlockState(pos.offset(direction), state);
            updateRedstone(state, EnumSet.of(direction), true);
            this.markDirty();
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        for (Direction direction : Direction.values()) {
            setRedstoneOutput(direction, compound.getInt(direction.name() + "Power"));
        }
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        int i = 0;
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.name() + "Power", power[i]);
            i++;
        }
        return compound;
    }

    public Direction getDirecton(ComputerSide computerSide) {
        Direction output = Direction.DOWN;
        if (computerSide == ComputerSide.FRONT) output = getBlockState().get(RedstoneIntegratorBlock.FACING);
        if (computerSide == ComputerSide.BACK)
            output = getBlockState().get(RedstoneIntegratorBlock.FACING).getOpposite();
        if (computerSide == ComputerSide.TOP) output = Direction.UP;
        if (computerSide == ComputerSide.BOTTOM) output = Direction.DOWN;
        if (computerSide == ComputerSide.RIGHT)
            output = getBlockState().get(RedstoneIntegratorBlock.FACING).rotateYCCW();
        if (computerSide == ComputerSide.LEFT) output = getBlockState().get(RedstoneIntegratorBlock.FACING).rotateY();
        return output;
    }
}
