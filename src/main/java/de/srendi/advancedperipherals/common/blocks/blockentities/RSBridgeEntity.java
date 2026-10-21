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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RSBridgeEntity extends PeripheralBlockEntity<RSBridgePeripheral> implements NetworkNodeContainerProvider, TaskStatusListener {

    private final NetworkNode node;
    private final InWorldNetworkNodeContainer networkNodeContainer;
    private final Map<Integer, RSCraftJob> jobs = new ConcurrentHashMap<>();
    private boolean addedListener = false;

    public RSBridgeEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.RS_BRIDGE.get(), pos, state);
        ConnectionStrategy connectionStrategy = new SimpleConnectionStrategy(pos);
        node = new SimpleNetworkNode(APConfig.PERIPHERALS_CONFIG.rsConsumption.get());
        networkNodeContainer = new InWorldNetworkNodeContainerImpl(this, node, "RS Bridge", 1, connectionStrategy, null);
    }

    @Override
    @NotNull
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

        for (RSCraftJob job : jobs.values()) {
            job.tick();
            if (job.canBePurged()) {
                jobs.remove(job.getId());
            }
        }
    }

    public void addJob(RSCraftJob job) {
        jobs.put(job.getId(), job);
    }

    public RSCraftJob getJob(int id) {
        return jobs.get(id);
    }

    public Collection<RSCraftJob> getJobs() {
        return jobs.values();
    }

    public NetworkNode getNode() {
        return node;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        initialize(this.level, null);
    }

    @Override
    @NotNull
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
        UUID id = taskStatus.info().id().id();
        notifyJobStateChanged(id);
    }

    @Override
    public void taskRemoved(@NotNull TaskId taskId) {
        UUID id = taskId.id();
        notifyJobStateChanged(id);
    }

    @Override
    public void taskAdded(@NotNull TaskStatus taskStatus) {
        UUID id = taskStatus.info().id().id();
        notifyJobStateChanged(id);
    }

    private void notifyJobStateChanged(UUID id) {
        jobs.values().stream()
            .filter(BasicCraftJob::isCraftingStarted)
            .filter(job -> id.equals(job.getUUID()))
            .forEach(BasicCraftJob::jobStateChanged);
    }
}
