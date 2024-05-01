package de.srendi.advancedperipherals.common.blocks.blockentities;

import dan200.computercraft.shared.util.RedstoneUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RedstoneIntegratorEntity extends PeripheralBlockEntity<RedstoneIntegratorPeripheral> {

    public int[] power = new int[Direction.values().length];

    public RedstoneIntegratorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.REDSTONE_INTEGRATOR.get(), pos, state);
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
        return neighbourState.getBlock() == Blocks.REDSTONE_WIRE ? Math.max(power, neighbourState.getValue(RedStoneWireBlock.POWER)) : power;
    }

    private void setRedstoneOutput(Direction direction, int power) {
        int old = this.power[direction.get3DDataValue()];
        this.power[direction.get3DDataValue()] = power;
        if (old != power) {
            if (level != null)
                RedstoneUtil.propagateRedstoneOutput(level, getBlockPos(), direction);

            this.setChanged();
        }
    }

    /**
     * Used to run redstone integrator functions not on the main thread to prevent long execution times
     * See <a href="https://github.com/SirEndii/AdvancedPeripherals/issues/384">#384</a>
     *
     * @param direction Cardinal direction
     * @param power     The redstone power from 0 to 15
     */
    public void setOutput(Direction direction, int power) {
        ServerWorker.add(() -> setRedstoneOutput(direction, power));
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        for (Direction direction : Direction.values()) {
            setRedstoneOutput(direction, compound.getInt(direction.name() + "Power"));
        }
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        int i = 0;
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.name() + "Power", power[i]);
            i++;
        }
    }

}
