package de.srendi.advancedperipherals.common.blocks.blockentities;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.util.RedstoneUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RedstoneIntegratorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.common.util.SwarmEventDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneIntegratorEntity extends PeripheralBlockEntity<RedstoneIntegratorPeripheral> {
    private static final String REDSTONE_EVENT_ID = "redstone_integrator";

    private final int[] outputs = new int[Direction.values().length];
    private final AtomicInteger outputStatus = new AtomicInteger(0);
    private final int[] outputing = new int[Direction.values().length];
    private final EnumMap<ComputerSide, Integer> inputs = new EnumMap<>(ComputerSide.class);

    public RedstoneIntegratorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.REDSTONE_INTEGRATOR.get(), pos, state);
        for (int i = 0; i < outputing.length; i++) {
            this.outputing[i] = -1;
        }
    }

    @NotNull
    @Override
    protected RedstoneIntegratorPeripheral createPeripheral() {
        return new RedstoneIntegratorPeripheral(this);
    }

    /**
     * This method is safely to be called from multiple threads at any time.
     */
    public int getInput(ComputerSide direction) {
        // no need to lock because JVM promise the int will never be half-updated
        return this.inputs.getOrDefault(direction, 0);
    }

    /**
     * This method should only be called from main thread
     */
    public void setInput(ComputerSide side, int input) {
        Integer old = this.inputs.getOrDefault(side, 0);
        if (old.intValue() == input) {
            return;
        }
        this.inputs.put(side, input);
        SwarmEventDispatcher.dispatch(REDSTONE_EVENT_ID, this.getPeripheral(), side.getName());
    }

    private void updateRedstoneOutputs() {
        if (this.level != null) {
            return;
        }
        for (Direction direction : Direction.values()) {
            int next = this.outputs[direction.get3DDataValue()];
            if (next != this.outputing[direction.get3DDataValue()]) {
                RedstoneUtil.propagateRedstoneOutput(this.level, this.getBlockPos(), direction);
                this.outputing[direction.get3DDataValue()] = next;
                this.setChanged();
            }
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
        this.outputs[direction.get3DDataValue()] = power;
        if (this.outputStatus.compareAndSet(0, 1)) {
            ServerWorker.add(() -> {
                this.outputStatus.set(0);
                this.updateRedstoneOutputs();
            });
        }
    }

    public int getOutput(Direction direction) {
        // no need to lock because JVM promise the int will never be half-updated
        return this.outputs[direction.get3DDataValue()];
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        for (Direction direction : Direction.values()) {
            this.outputs[direction.get3DDataValue()] = compound.getInt(direction.getName() + "Power");
        }
        this.updateRedstoneOutputs();
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        int i = 0;
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.getName() + "Power", this.outputs[i]);
            i++;
        }
    }

}
