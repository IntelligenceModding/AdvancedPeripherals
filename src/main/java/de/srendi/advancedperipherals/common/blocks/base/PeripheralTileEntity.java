package de.srendi.advancedperipherals.common.blocks.base;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

@MethodsReturnNonnullByDefault
public abstract class PeripheralTileEntity<T extends BasePeripheral> extends LockableTileEntity implements ISidedInventory, INamedContainerProvider  {

    private final LazyOptional<? extends IItemHandler> handler = LazyOptional.of(() -> new SidedInvWrapper(this, Direction.NORTH));
    protected NonNullList<ItemStack> items;

    protected T peripheral = createPeripheral();
    private LazyOptional<IPeripheral> peripheralCap;

    public PeripheralTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        if(this instanceof IInventoryBlock) {
            items = NonNullList.withSize(((IInventoryBlock<?>) this).getInvSize(), ItemStack.EMPTY);
        } else {
            items = NonNullList.withSize(0, ItemStack.EMPTY);
        }
    }

    @Override
    public <T1> LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (peripheral.isEnabled()) {
            if (cap == CAPABILITY_PERIPHERAL) {
                if (peripheralCap == null) {
                    peripheralCap = LazyOptional.of(()->peripheral);
                }
                return peripheralCap.cast();
            }
        } else {
            AdvancedPeripherals.Debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !removed && direction != null && this instanceof IInventoryBlock) {
            return handler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (peripheralCap != null)
            peripheralCap.invalidate();
        handler.invalidate();
    }

    protected abstract T createPeripheral();

    public List<IComputerAccess> getConnectedComputers() {
        return peripheral.getConnectedComputers();
    }

    /*@Override
    public ITextComponent getDisplayName() {
        return this instanceof IInventoryBlock ? ((IInventoryBlock) this).getDisplayName() : null;
    }*/

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, items);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        ItemStackHelper.loadAllItems(compound, items);
        super.read(state, compound);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return this instanceof IInventoryBlock ? ((IInventoryBlock<?>) this).getDisplayName() : null;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory inventory, @NotNull PlayerEntity playerEntity) {
        return createMenu(id, inventory);
    }

    @Override
    protected Container createMenu(int id, @NotNull PlayerInventory player) {
        return this instanceof IInventoryBlock ? ((IInventoryBlock<?>) this).createContainer(id, player, pos, world) : null;
    }

    @Override
    public int [] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemStack : items) {
            if(itemStack.isEmpty())
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @NotNull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(items, index, count);
    }

    @NotNull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(items, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
    }
}

