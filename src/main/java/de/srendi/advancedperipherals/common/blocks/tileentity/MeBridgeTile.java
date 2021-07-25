package de.srendi.advancedperipherals.common.blocks.tileentity;

import appeng.api.config.Actionable;
import appeng.api.networking.*;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import com.google.common.collect.ImmutableSet;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class MeBridgeTile extends PeripheralTileEntity<MeBridgePeripheral> implements ICraftingRequester, ITickableTileEntity, IGridBlock, IActionHost, IActionSource, IGridHost {

    private IGridNode node;
    private PlayerEntity placed;
    private boolean initialized;

    public MeBridgeTile() {
        super(TileEntityTypes.ME_BRIDGE.get());
    }

    public void setPlayer(PlayerEntity placed) {
        this.placed = placed;
    }

    @NotNull
    @Override
    protected MeBridgePeripheral createPeripheral() {
        return new MeBridgePeripheral("meBridge", this, this);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (node != null) {
            node.destroy();
            node = null;
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (!initialized) {
                if (AppEngApi.INSTANCE.getApi() != null) {
                    node = AppEngApi.INSTANCE.getApi().grid().createGridNode(this);
                    if (placed != null) {
                        node.setPlayerID(AppEngApi.INSTANCE.getApi().registries().players().getID(placed));
                    }
                    node.updateState();
                }
                if (peripheral == null) // should never happen, but still, in any case
                    peripheral = createPeripheral();
                peripheral.setNode(node);
                initialized = true;
            }
        }
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
        return new ItemStack(Blocks.ME_BRIDGE.get());
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
        return AECableType.NONE;
    }

    @Override
    public void securityBreak() {
    }

    @NotNull
    @Override
    public Optional<PlayerEntity> player() {
        return Optional.empty();
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


    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return ImmutableSet.of();
    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink iCraftingLink, IAEItemStack iaeItemStack, Actionable actionable) {
        return iaeItemStack;
    }

    @Override
    public void jobStateChange(ICraftingLink iCraftingLink) {
    }

}