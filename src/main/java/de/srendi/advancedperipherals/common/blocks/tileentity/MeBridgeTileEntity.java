package de.srendi.advancedperipherals.common.blocks.tileentity;

import appeng.api.AEAddon;
import appeng.api.IAEAddon;
import appeng.api.IAppEngApi;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.setup.ModBlocks;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class MeBridgeTileEntity extends TileEntity implements IGridBlock, IActionHost, ITickableTileEntity, IActionSource, IGridHost {

    private IGridNode node;
    private boolean initialized = false;
    private PlayerEntity placed;

    public MeBridgeTileEntity() {
        this(ModTileEntityTypes.ME_BRIDGE.get());
    }

    public MeBridgeTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public void setPlayer(PlayerEntity placed) {
        this.placed = placed;
    }

    @Override
    public double getIdlePowerUsage() {
        return 500;
    }

    @NotNull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isWorldAccessible() {
        return true;
    }

    @NotNull
    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @NotNull
    @Override
    public AEColor getGridColor() {
        return AEColor.TRANSPARENT;
    }

    @Override
    public void onGridNotification(@NotNull GridNotification gridNotification) {

    }

    @NotNull
    @Override
    public EnumSet<Direction> getConnectableSides() {
        return EnumSet.allOf(Direction.class);
    }

    @NotNull
    @Override
    public IGridHost getMachine() {
        return this;
    }

    @Override
    public void gridChanged() {

    }

    @NotNull
    @Override
    public ItemStack getMachineRepresentation() {
        return new ItemStack(ModBlocks.ME_BRIDGE.get());
    }

    @Nullable
    @Override
    public IGridNode getActionableNode() {
        return node;
    }

    @Nullable
    @Override
    public IGridNode getGridNode(@NotNull AEPartLocation aePartLocation) {
        return node;
    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull AEPartLocation aePartLocation) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public void tick() {
        if (!initialized) {
            if (AppEngApi.INSTANCE.getApi() != null) {
                node = AppEngApi.INSTANCE.getApi().grid().createGridNode(this);
                if (placed != null) {
                    node.setPlayerID(AppEngApi.INSTANCE.getApi().registries().players().getID(placed));
                }
                node.updateState();
            } else {
                //Just a debug message
                AdvancedPeripherals.LOGGER.log(Level.DEBUG, "Debug: api is null");
            }
            initialized = true;
        }

    }

    @NotNull
    @Override
    public Optional<PlayerEntity> player() {
        return Optional.of(placed);
    }

    @NotNull
    @Override
    public Optional<IActionHost> machine() {
        return Optional.of(this);
    }

    @NotNull
    @Override
    public <T> Optional<T> context(@NotNull Class<T> aClass) {
        return Optional.empty();
    }
}