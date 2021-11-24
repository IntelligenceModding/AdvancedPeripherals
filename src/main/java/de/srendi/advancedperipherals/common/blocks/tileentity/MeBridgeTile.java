package de.srendi.advancedperipherals.common.blocks.tileentity;

import appeng.api.networking.*;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.util.AECableType;
import appeng.blockentity.storage.DriveBlockEntity;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeBridgeEntityListener;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

public class MeBridgeTile extends PeripheralTileEntity<MeBridgePeripheral> implements IActionSource, IActionHost, IInWorldGridNodeHost, ICraftingSimulationRequester {

    private boolean initialized = false;

    private final List<CraftJob> jobs = new ArrayList<>();

    public MeBridgeTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.ME_BRIDGE.get(), pos, state);
    }

    private IManagedGridNode mainNode = GridHelper.createManagedNode(this, MeBridgeEntityListener.INSTANCE);

    @NotNull
    @Override
    protected MeBridgePeripheral createPeripheral() {
        return new MeBridgePeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!this.level.isClientSide) {
            if (!initialized) {

                mainNode.setFlags(GridFlags.REQUIRE_CHANNEL);
                mainNode.setIdlePowerUsage(APConfig.PERIPHERALS_CONFIG.ME_CONSUMPTION.get());
                mainNode.setVisualRepresentation(new ItemStack(Blocks.ME_BRIDGE.get()));
                mainNode.setInWorldNode(true);
                mainNode.create(level, getBlockPos());

                //peripheral can be null if `getCapability` was not called before
                if (peripheral == null)
                    peripheral = createPeripheral();
                peripheral.setNode(mainNode);
                initialized = true;
                AdvancedPeripherals.debug("DEBUG2 " + mainNode.isReady(), org.apache.logging.log4j.Level.ERROR);
            }
            for(CraftJob job : jobs) {
                job.maybeCraft();
            }
            jobs.removeIf(CraftJob::isCraftingStarted);
        }
    }

    @Nonnull
    @Override
    public Optional<Player> player() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IActionHost> machine() {
        return Optional.of(this);
    }

    @Nonnull
    @Override
    public <T> Optional<T> context(@Nonnull Class<T> key) {
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
    public IGridNode getGridNode(@Nonnull Direction dir) {
        return getActionableNode();
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull Direction dir) {
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
}