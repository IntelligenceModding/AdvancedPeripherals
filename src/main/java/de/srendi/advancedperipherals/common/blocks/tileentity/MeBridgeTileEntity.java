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
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral;
import de.srendi.advancedperipherals.common.setup.ModBlocks;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class MeBridgeTileEntity extends TileEntity implements ICraftingRequester, ITickableTileEntity, IGridBlock, IActionHost, IActionSource, IGridHost {

    private AppEngApi aeAPI = AppEngApi.INSTANCE;
    private IGridNode node;
    private PlayerEntity placed;
    private boolean initialized;
    private MeBridgePeripheral peripheral;
    private LazyOptional<IPeripheral> peripheralCap;

    public MeBridgeTileEntity() {
        this(ModTileEntityTypes.ME_BRIDGE.get());
        peripheral = new MeBridgePeripheral("meBridge", this, this);
    }

    public MeBridgeTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public List<IComputerAccess> getConnectedComputers() {
        return peripheral.getConnectedComputers();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(()->peripheral);
            }
            return peripheralCap.cast();
        }

        return super.getCapability(cap, direction);
    }

    public void setPlayer(PlayerEntity placed) {
        this.placed = placed;
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (node != null) {
            node.destroy();
            node = null;
        }
        if(peripheralCap != null)
            peripheralCap.invalidate();
    }

    @Override
    public void tick() {
        if (!getWorld().isRemote) {
            if (!initialized) {
                if (AppEngApi.INSTANCE.getApi() != null) {
                    node = AppEngApi.INSTANCE.getApi().grid().createGridNode(this);
                    if (placed != null) {
                        node.setPlayerID(AppEngApi.INSTANCE.getApi().registries().players().getID(placed));
                    }
                    node.updateState();
                }
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