package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralBlockEntity<T extends BasePeripheral<?>> extends BaseContainerBlockEntity implements WorldlyContainer, IPeripheralBlockEntity, BlockCapabilityProviders.ItemHandler, BlockCapabilityProviders.Peripheral, VarNameable {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    protected CompoundTag peripheralSettings = new CompoundTag();
    protected NonNullList<ItemStack> items;
    private IItemHandler itemHandler = null;
    private IPeripheral peripheral = null;

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

    @Override
    @NotNull
    public IItemHandler createItemHandlerCap(@Nullable Direction side) {
        if (this.itemHandler == null) {
            this.itemHandler = new SidedInvWrapper(this, null);
        }
        return this.itemHandler;
    }

    @Override
    @NotNull
    public IPeripheral createPeripheralCap(@Nullable Direction side) {
        // Perform later peripheral creation, because creating peripheral
        // on init of tile entity cause some infinity loop, if peripheral
        // are depend on tile entity data
        if (this.peripheral == null) {
            // Recreate peripheral to allow CC: Tweaked correctly handle
            // peripheral update logic, so new peripheral and old one will be
            // different
            this.peripheral = this.createPeripheralOrDisabled();
        }
        return this.peripheral;
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
        IPeripheral peripheral = this.createPeripheralCap(null);
        if (peripheral == null || peripheral instanceof DisabledPeripheral) {
            return null;
        }
        return (T) peripheral;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ContainerHelper.loadAllItems(tag, items, provider);
        peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        super.loadAdditional(tag, provider);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.saveShared(tag, provider);
        ContainerHelper.saveAllItems(tag, items, provider);
        if (!peripheralSettings.isEmpty()) {
            tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag data = super.getUpdateTag(provider);
        this.saveShared(data, provider);
        return data;
    }

    protected void saveShared(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {}

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
    @NotNull
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
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
