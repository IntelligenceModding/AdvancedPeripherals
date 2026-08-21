package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralBlockEntity<T extends BasePeripheral<?>> extends BaseContainerBlockEntity implements WorldlyContainer, IPeripheralBlockEntity, VarNameable {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    protected CompoundTag peripheralSettings = new CompoundTag();
    protected NonNullList<ItemStack> items;
    private LazyOptional<? extends IItemHandler> itemHandler = LazyOptional.empty();
    private LazyOptional<IPeripheral> peripheralCap = LazyOptional.empty();

    protected PeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (this instanceof IInventoryBlock inventoryBlock) {
            items = NonNullList.withSize(inventoryBlock.getInvSize(), ItemStack.EMPTY);
        } else {
            items = NonNullList.withSize(0, ItemStack.EMPTY);
        }
    }

    @Override
    public void setName(Component name) {
        this.name = name;
        this.setChanged();
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction direction) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL) {
            return this.getLazyPeripheral().cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (!remove && direction != null && this instanceof IInventoryBlock) {
                if (!this.itemHandler.isPresent()) {
                    this.itemHandler = LazyOptional.of(() -> new SidedInvWrapper(this, Direction.NORTH));
                }
                return this.itemHandler.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandler.invalidate();
        this.peripheralCap.invalidate();
    }

    @NotNull
    public LazyOptional<IPeripheral> getLazyPeripheral() {
        // Perform later peripheral creation, because creating peripheral
        // on init of tile entity cause some infinity loop, if peripheral
        // are depend on tile entity data
        if (!this.peripheralCap.isPresent()) {
            // Recreate peripheral to allow CC: Tweaked correctly handle
            // peripheral update logic, so new peripheral and old one will be
            // different
            this.peripheralCap = LazyOptional.of(this::createPeripheralOrDisabled);
        }
        return this.peripheralCap;
    }

    @NotNull
    protected abstract T buildPeripheral();

    protected IPeripheral createPeripheralOrDisabled() {
        T peripheral = this.buildPeripheral();
        return peripheral.isEnabled() ? peripheral : new DisabledPeripheral(peripheral);
    }

    public void queueEvent(String event, Object... args) {
        if (this.getLevel().isClientSide()) {
            return;
        }
        @Nullable T peripheral = this.getPeripheral();
        if (peripheral != null) {
            peripheral.queueEvent(event, args);
        }
    }

    @Nullable
    public T getPeripheral() {
        IPeripheral peripheral = this.getLazyPeripheral().orElse(null);
        if (peripheral == null || peripheral instanceof DisabledPeripheral) {
            return null;
        }
        return (T) peripheral;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, items);
        peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        super.load(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        this.saveShared(tag);
        ContainerHelper.saveAllItems(tag, items);
        if (!peripheralSettings.isEmpty()) {
            tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag data = super.getUpdateTag();
        this.saveShared(data);
        return data;
    }

    protected void saveShared(@NotNull CompoundTag tag) {}

    @Override
    protected Component getDefaultName() {
        return this instanceof IInventoryMenuBlock inventoryBlock ? inventoryBlock.getDisplayName() : null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return null;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    @Override
    @NotNull
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @Override
    @NotNull
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(items, index, count);
    }

    @Override
    @NotNull
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

    @Override
    public CompoundTag getPeripheralSettings() {
        return this.peripheralSettings;
    }

    @Override
    public void setPeripheralSettings(CompoundTag tag) {
        this.peripheralSettings = tag;
        this.markSettingsChanged();
    }

    @Override
    public void markSettingsChanged() {
        this.setChanged();
    }

    @Override
    public <U extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<U> type) {
        if (level.isClientSide()) {
            return;
        }
        @Nullable T peripheral = this.getPeripheral();
        if (peripheral != null) {
            peripheral.update();
        }
    }

    public void sendUpdate() {
        if (this.getLevel().isClientSide) {
            return;
        }
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0 /* no use on server-side */);
    }
}
