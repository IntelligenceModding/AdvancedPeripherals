package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.Capabilities;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public abstract class PeripheralBlockEntity<T extends BasePeripheral<?>> extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider, IPeripheralTileEntity {

    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    protected CompoundTag peripheralSettings = new CompoundTag();
    protected NonNullList<ItemStack> items;
    private LazyOptional<? extends IItemHandler> handler = LazyOptional.empty();
    private LazyOptional<? extends IFluidHandler> fluidHandler = LazyOptional.empty();
    private LazyOptional<T> peripheralCap = LazyOptional.empty();

    protected PeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (this instanceof IInventoryBlock inventoryBlock) {
            items = NonNullList.withSize(inventoryBlock.getInvSize(), ItemStack.EMPTY);
        } else {
            items = NonNullList.withSize(0, ItemStack.EMPTY);
        }
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction direction) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL) {
            return this.getLazyPeripheral().cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (!remove && direction != null && this instanceof IInventoryBlock) {
                if (!handler.isPresent()) {
                    handler = LazyOptional.of(() -> new SidedInvWrapper(this, Direction.NORTH));
                }
                return handler.cast();
            }
        } else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (!remove && direction != null) {
                if (!fluidHandler.isPresent()) {
                    fluidHandler = LazyOptional.of(() -> new FluidTank(0));
                }
                return fluidHandler.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        peripheralCap.invalidate();
        handler.invalidate();
        fluidHandler.invalidate();
    }

    @NotNull
    protected abstract T createPeripheral();

    public Iterable<IComputerAccess> getConnectedComputers() {
        return this.getLazyPeripheral().map(BasePeripheral::getConnectedComputers).orElse(Collections.emptyList());
    }

    public LazyOptional<T> getLazyPeripheral() {
        // Perform later peripheral creation, because creating peripheral
        // on init of tile entity cause some infinity loop, if peripheral
        // are depend on tile entity data
        if (!this.peripheralCap.isPresent()) {
            // Recreate peripheral to allow CC: Tweaked correctly handle
            // peripheral update logic, so new peripheral and old one will be
            // different
            final T peripheral = createPeripheral();
            if (!peripheral.isEnabled()) {
                AdvancedPeripherals.debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
                return LazyOptional.empty();
            }
            this.peripheralCap = LazyOptional.of(() -> peripheral);
        }
        return this.peripheralCap;
    }

    @Nullable
    public T getPeripheral() {
        return this.getLazyPeripheral().orElse(null);
    }

    /*@Override
    public ITextComponent getDisplayName() {
        return this instanceof IInventoryBlock ? ((IInventoryBlock) this).getDisplayName() : null;
    }*/

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        if (!peripheralSettings.isEmpty()) {
            compound.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        ContainerHelper.loadAllItems(compound, items);
        peripheralSettings = compound.getCompound(PERIPHERAL_SETTINGS_KEY);
        super.load(compound);
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return this instanceof IInventoryMenuBlock<?> inventoryBlock ? inventoryBlock.getDisplayName() : null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player playerEntity) {
        return createMenu(id, inventory);
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return this instanceof IInventoryMenuBlock<?> inventoryBlock ? inventoryBlock.createContainer(id, player, worldPosition, level) : null;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) return true;
        }
        return false;
    }


    @NotNull
    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(items, index, count);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    public CompoundTag getPeripheralSettings() {
        return peripheralSettings;
    }

    @Override
    public void markSettingsChanged() {
        this.setChanged();
    }

    protected void sendUpdate() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 11);
    }

    public ComputerSide getComputerSide(Direction direction) {
        FrontAndTop orientation = getBlockState().getValue(BaseBlock.ORIENTATION);
        return CoordUtil.getComputerSide(orientation, direction);
    }
}
