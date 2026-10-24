package de.srendi.advancedperipherals.common.blocks.blockentities;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.util.AECableType;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.google.common.collect.ImmutableSet;
import de.srendi.advancedperipherals.common.addons.ae2.AECraftJob;
import de.srendi.advancedperipherals.common.addons.ae2.MEBridgeEntityListener;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MEBridgeEntity extends PeripheralBlockEntity<MEBridgePeripheral> implements IActionSource, IGridConnectedBlockEntity, ICraftingSimulationRequester, ICraftingRequester {

    private final Map<Integer, AECraftJob> jobs = new ConcurrentHashMap<>();
    private final Map<UUID, WeakReference<AECraftJob>> watchingJob = new ConcurrentHashMap<>();
    private boolean initialized = false;
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, MEBridgeEntityListener.INSTANCE);

    public MEBridgeEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ME_BRIDGE.get(), pos, state);
        getMainNode().setExposedOnSides(getGridConnectableSides(null));
    }

    @Override
    @NotNull
    protected MEBridgePeripheral buildPeripheral() {
        return new MEBridgePeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        super.handleTick(level, state, type);
        if (level.isClientSide()) {
            return;
        }
        if (!initialized) {
            initialized = true;
            mainNode.setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(APConfig.PERIPHERALS_CONFIG.meConsumption.get())
                .setVisualRepresentation(new ItemStack(APBlocks.ME_BRIDGE.get()))
                .setInWorldNode(true)
                .addService(ICraftingRequester.class, this)
                .create(level, getBlockPos());
            MEBridgePeripheral peripheral = this.getPeripheral();
            if (peripheral != null) {
                peripheral.setNode(mainNode);
            }
        }

        for (AECraftJob job : jobs.values()) {
            job.tick();
            if (job.canBePurged()) {
                jobs.remove(job.getId());
            }
        }
        for (Map.Entry<UUID, WeakReference<AECraftJob>> entry : watchingJob.entrySet()) {
            AECraftJob job = entry.getValue().get();
            if (job == null) {
                watchingJob.remove(entry.getKey());
                continue;
            }
            job.tick();
            if (job.canBePurged()) {
                watchingJob.remove(entry.getKey());
            }
        }
    }

    @Override
    @NotNull
    public Optional<Player> player() {
        return Optional.empty();
    }

    @Override
    @NotNull
    public Optional<IActionHost> machine() {
        return Optional.of(this);
    }

    @Override
    @NotNull
    public <T> Optional<T> context(Class<T> key) {
        return Optional.empty();
    }

    @Override
    @Nullable
    public IGridNode getActionableNode() {
        return getGridNode();
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

    @Override
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    @Override
    @Nullable
    public IGridNode getGridNode() {
        return mainNode.getNode();
    }

    @Override
    @Nullable
    public IGridNode getGridNode(Direction dir) {
        return getGridNode();
    }

    @Override
    public void saveChanges() {
    }

    @Override
    @NotNull
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    /**
     * Return the current action source, used to extract items.
     */
    @Override
    @Nullable
    public IActionSource getActionSource() {
        return this;
    }

    public void addJob(AECraftJob job) {
        jobs.put(job.getId(), job);
    }

    public void watchJob(AECraftJob job) {
        if (job.getId() != -1) {
            throw new IllegalArgumentException("Should not watch already watching job.");
        }
        watchingJob.put(job.getUUID(), new WeakReference<>(job));
    }

    public AECraftJob getJob(int id) {
        return jobs.get(id);
    }

    public AECraftJob getJob(UUID id) {
        WeakReference<AECraftJob> ref = watchingJob.get(id);
        if (ref != null) {
            AECraftJob job = ref.get();
            if (job != null) {
                return job;
            }
        }
        return jobs.values().stream().filter(j -> id.equals(j.getUUID())).findFirst().orElse(null);
    }

    public Collection<AECraftJob> getRunningJobs() {
        return jobs.values();
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return jobs.values().stream()
            .filter(BasicCraftJob::isCraftingStarted)
            .map(AECraftJob::getJobLink)
            .collect(Collectors.toCollection(ImmutableSet::of));
    }

    @Override
    public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
        return 0;
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        AECraftJob job = this.getJob(link.getCraftingID());
        if (job != null) {
            job.jobStateChanged();
            jobs.remove(job.getId());
        }
    }
}
