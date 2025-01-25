package de.srendi.advancedperipherals.common.blocks.blockentities;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSCraftJob;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class RsBridgeEntity extends NetworkNodeBlockEntity<RefinedStorageNode> implements INetworkNodeProxy<RefinedStorageNode>, IRedstoneConfigurable, IPeripheralTileEntity, ICraftingMonitorListener {

    private static final String PERIPHERAL_SETTINGS = "AP_SETTINGS";
    //I have no clue what this does, but it works
    private static final BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder().build();
    protected CompoundTag peripheralSettings;
    protected RsBridgePeripheral peripheral = new RsBridgePeripheral(this);
    private LazyOptional<IPeripheral> peripheralCap;
    private final List<RSCraftJob> jobs = new CopyOnWriteArrayList<>();
    private boolean addedListener = false;

    public RsBridgeEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.RS_BRIDGE.get(), pos, state, SPEC);
        peripheralSettings = new CompoundTag();
    }

    @NotNull
    public <T1> LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheral.isEnabled()) {
                if (peripheralCap == null) {
                    peripheralCap = LazyOptional.of(() -> peripheral);
                }
                return peripheralCap.cast();
            } else {
                AdvancedPeripherals.debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (getNode().getNetwork() != null) {
            ICraftingManager manager = getNode().getNetwork().getCraftingManager();
            if (manager != null && !this.addedListener) {
                manager.addListener(this);
                this.addedListener = true;
            } else if (manager == null && this.addedListener) {
                this.addedListener = false;
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

    public RefinedStorageNode createNode(Level level, BlockPos blockPos) {
        return new RefinedStorageNode(level, blockPos);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        if (!peripheralSettings.isEmpty()) compound.put(PERIPHERAL_SETTINGS, peripheralSettings);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        peripheralSettings = compound.getCompound(PERIPHERAL_SETTINGS);
        super.load(compound);
    }

    @Override
    public CompoundTag getPeripheralSettings() {
        return peripheralSettings;
    }

    @Override
    public void markSettingsChanged() {
        setChanged();
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onChanged() {
        jobs.stream().filter(BasicCraftJob::isCraftingStarted).forEach(BasicCraftJob::jobStateChanged);
    }
}
