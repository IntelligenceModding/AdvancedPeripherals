package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.shared.util.RedstoneUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RedstoneIntegratorTile extends PeripheralTileEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorTile() {
        super(TileEntityTypes.REDSTONE_INTEGRATOR.get());
    }

    @NotNull
    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral(this);
    }

    public int getRedstoneInput(Direction direction) {
        Objects.requireNonNull(level);
        BlockPos neighbourPos = getBlockPos().relative(direction);
        int power = level.getSignal(neighbourPos, direction);
        if (power >= 15) return power;

        BlockState neighbourState = level.getBlockState(neighbourPos);
        return neighbourState.getBlock() == Blocks.REDSTONE_WIRE
                ? Math.max(power, neighbourState.getValue(RedstoneWireBlock.POWER)) : power;
    }

    public void setRedstoneOutput(Direction direction, int power) {
        int old = this.power[direction.get3DDataValue()];
        this.power[direction.get3DDataValue()] = power;
        if (old != power) {
            if (level != null)
                RedstoneUtil.propagateRedstoneOutput(level, getBlockPos(), direction);

            this.setChanged();
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        for (Direction direction : Direction.values()) {
            setRedstoneOutput(direction, compound.getInt(direction.name() + "Power"));
        }
        super.load(state, compound);
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

}
