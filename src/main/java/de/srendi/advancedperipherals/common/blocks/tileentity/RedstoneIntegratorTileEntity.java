package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class RedstoneIntegratorTileEntity extends PeripheralTileEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorTileEntity() {
        super(TileEntityTypes.REDSTONE_INTEGRATOR.get());
    }

    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral("redstoneIntegrator", this);
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
        if (computerSide == ComputerSide.FRONT)
            return getBlockState().get(RedstoneIntegratorBlock.FACING).getOpposite();
        if (computerSide == ComputerSide.BACK) return getBlockState().get(RedstoneIntegratorBlock.FACING);
        if (computerSide == ComputerSide.TOP) return Direction.UP;
        if (computerSide == ComputerSide.BOTTOM) return Direction.DOWN;
        if (computerSide == ComputerSide.RIGHT) return getBlockState().get(RedstoneIntegratorBlock.FACING).rotateY();
        if (computerSide == ComputerSide.LEFT) return getBlockState().get(RedstoneIntegratorBlock.FACING).rotateYCCW();
        return Direction.DOWN;
    }
}
