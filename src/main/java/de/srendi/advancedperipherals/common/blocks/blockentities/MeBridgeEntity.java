package de.srendi.advancedperipherals.common.blocks.blockentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageHelper;
import appeng.api.util.AECableType;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeBridgeEntityListener;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.IInventoryBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeBridgeEntity extends PeripheralBlockEntity<MeBridgePeripheral> implements IActionSource, IActionHost, IInWorldGridNodeHost, ICraftingSimulationRequester, IInventoryBlock {

    private final List<CraftJob> jobs = new CopyOnWriteArrayList<>();
    private boolean initialized = false;
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, MeBridgeEntityListener.INSTANCE);

    public MeBridgeEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ME_BRIDGE.get(), pos, state);
    }

    @NotNull
    @Override
    protected MeBridgePeripheral createPeripheral() {
        return new MeBridgePeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!this.level.isClientSide) {
            if (!initialized) {
                MeBridgePeripheral peripheral = this.getPeripheral();
                if (peripheral == null) {
                    return;
                }

                mainNode.setFlags(GridFlags.REQUIRE_CHANNEL);
                mainNode.setIdlePowerUsage(APConfig.PERIPHERALS_CONFIG.meConsumption.get());
                mainNode.setVisualRepresentation(new ItemStack(APBlocks.ME_BRIDGE.get()));
                mainNode.setInWorldNode(true);
                mainNode.create(level, getBlockPos());

                peripheral.setNode(mainNode);
                initialized = true;
            }

            // Try to start the job if the job calculation finished
            jobs.forEach(CraftJob::maybeCraft);

            // Remove the job if the crafting started, we can't do anything with it anymore
            jobs.removeIf(CraftJob::isCraftingStarted);
        }
    }

    @NotNull
    @Override
    public Optional<Player> player() {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<IActionHost> machine() {
        return Optional.of(this);
    }

    @NotNull
    @Override
    public <T> Optional<T> context(@NotNull Class<T> key) {
        return Optional.empty();
    }

    @Nullable
    @Override
    public IGridNode getActionableNode() {
        return mainNode.getNode();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        mainNode.destroy();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        mainNode.destroy();
    }

    @Nullable
    @Override
    public IGridNode getGridNode(@NotNull Direction dir) {
        return getActionableNode();
    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull Direction dir) {
        return AECableType.SMART;
    }

    /**
     * Return the current action source, used to extract items.
     */
    @Nullable
    @Override
    public IActionSource getActionSource() {
        return this;
    }

    public void addJob(CraftJob job) {
        jobs.add(job);
    }

    @Override
    public int getInvSize() {
        return 9;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        super.setItem(index, stack);
        if (!initialized)
            return;

        if (stack.isEmpty())
            return;

        IEnergyService energySrc = mainNode.getGrid().getEnergyService();
        int inserted = (int) StorageHelper.poweredInsert(energySrc, AppEngApi.getMonitor(getActionableNode()), AEItemKey.of(stack), stack.getCount(), this);

        if (inserted > 0) {
            getItem(index).setCount(getItem(index).getCount() - inserted);
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return false;
    }

}
