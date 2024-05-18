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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

public class RedstoneIntegratorEntity extends PeripheralBlockEntity<RedstoneIntegratorPeripheral> {
    private static final String REDSTONE_EVENT_ID = "redstone_integrator";

    private final int[] outputs = new int[Direction.values().length];
    private final AtomicInteger outputStatus = new AtomicInteger(0);
    private final int[] outputing = new int[Direction.values().length];
    private final int[] inputs = new int[Direction.values().length];

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
    public int getInput(Direction direction) {
        // no need to lock because JVM promise the int will never be half-updated
        return this.inputs[direction.get3DDataValue()];
    }

    /**
     * This method should only be called from main thread
     */
    public void setInput(Direction direction, int input) {
        if (this.inputs[direction.get3DDataValue()] == input) {
            return;
        }
        this.inputs[direction.get3DDataValue()] = input;
        ComputerSide side = this.getComputerSide(direction);
        RedstoneIntegratorPeripheral peripheral = this.getPeripheral();
        if (peripheral != null) {
            SwarmEventDispatcher.dispatch(REDSTONE_EVENT_ID, peripheral, side.getName());
        }
    }

    private void updateRedstoneOutputs() {
        if (this.level == null) {
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
        power = Math.min(Math.max(power, 0), 15);
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

    private void initSignals() {
        for (Direction direction : Direction.values()) {
            this.setInput(direction, RedstoneUtil.getRedstoneInput(this.level, this.getBlockPos().relative(direction), direction));
            RedstoneUtil.propagateRedstoneOutput(this.level, this.getBlockPos(), direction);
            this.outputing[direction.get3DDataValue()] = this.outputs[direction.get3DDataValue()];
        }
        this.setChanged();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        ServerWorker.addToNextTick(this::initSignals);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        for (Direction direction : Direction.values()) {
            this.outputs[direction.get3DDataValue()] = compound.getInt(direction.getName() + "Power");
        }
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        for (Direction direction : Direction.values()) {
            compound.putInt(direction.getName() + "Power", this.outputs[direction.get3DDataValue()]);
        }
    }

}
