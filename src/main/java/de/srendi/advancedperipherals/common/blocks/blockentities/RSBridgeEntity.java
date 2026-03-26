package de.srendi.advancedperipherals.common.blocks.blockentities;

import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusListener;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskId;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.support.network.InWorldNetworkNodeContainerImpl;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RSBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSCraftJob;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class RSBridgeEntity extends PeripheralBlockEntity<RSBridgePeripheral> implements NetworkNodeContainerProvider, TaskStatusListener {

    private final NetworkNode node;
    private final InWorldNetworkNodeContainer networkNodeContainer;
    private final List<RSCraftJob> jobs = new CopyOnWriteArrayList<>();
    private boolean addedListener = false;

    public RSBridgeEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.RS_BRIDGE.get(), pos, state);
        ConnectionStrategy connectionStrategy = new SimpleConnectionStrategy(pos);
        node = new SimpleNetworkNode(APConfig.PERIPHERALS_CONFIG.rsConsumption.get());
        networkNodeContainer = new InWorldNetworkNodeContainerImpl(this, node, "RS Bridge", 1, connectionStrategy, null);
    }

    @NotNull
    @Override
    protected RSBridgePeripheral buildPeripheral() {
        return new RSBridgePeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        super.handleTick(level, state, type);
        if (level.isClientSide()) {
            return;
        }
        if (getNode().getNetwork() != null) {
            AutocraftingNetworkComponent manager = getNode().getNetwork().getComponent(AutocraftingNetworkComponent.class);
            if (!this.addedListener) {
                manager.addListener(this);
                this.addedListener = true;
            }
        }

        // Try to start the job if the job calculation finished
        jobs.forEach(BasicCraftJob::tick);

        // Remove the job if the crafting calculation failed, we can't do anything with it anymore
        jobs.removeIf(BasicCraftJob::canBePurged);
    }

    public void addJob(RSCraftJob job) {
        jobs.add(job);
    }

    public List<RSCraftJob> getJobs() {
        return jobs;
    }

    public NetworkNode getNode() {
        return node;
    }

    public void clearRemoved() {
        super.clearRemoved();
        initialize(this.level, null);
    }

    @NotNull
    @Override
    public Set<InWorldNetworkNodeContainer> getContainers() {
        return Set.of(networkNodeContainer);
    }

    @Override
    public void addContainer(@NotNull InWorldNetworkNodeContainer inWorldNetworkNodeContainer) {
    }

    @Override
    public boolean canBuild(@NotNull ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void taskStatusChanged(@NotNull TaskStatus taskStatus) {
        jobs.stream().filter(BasicCraftJob::isCraftingStarted).filter(job -> job.getCraftingTask().info().id().equals(taskStatus.info().id())).forEach(BasicCraftJob::jobStateChanged);
    }

    @Override
    public void taskRemoved(@NotNull TaskId taskId) {
        jobs.stream().filter(BasicCraftJob::isCraftingStarted).filter(job -> job.getCraftingTask().info().id().equals(taskId)).forEach(BasicCraftJob::jobStateChanged);
    }

    @Override
    public void taskAdded(@NotNull TaskStatus taskStatus) {
        jobs.stream().filter(BasicCraftJob::isCraftingStarted).filter(job -> job.getCraftingTask().info().id().equals(taskStatus.info().id())).forEach(BasicCraftJob::jobStateChanged);
    }
}
