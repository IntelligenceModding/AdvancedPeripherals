package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class InventoryManagerOwner extends BlockEntityPeripheralOwner<InventoryManagerEntity> implements IItemHandler {
    public InventoryManagerOwner(InventoryManagerEntity tile) {
        super(tile);
    }

    @Override
    @Nullable
    public Player getOwner() {
        return this.getBlockEntity().getOwnerPlayer();
    }

    @Nullable
    protected Inventory getInventory() {
        Player owner = this.getOwner();
        if (owner == null) {
            return null;
        }
        return owner.getInventory();
    }

    @Override
    public int getSlots() {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return 0;
        }
        return inv.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return ItemStack.EMPTY;
        }
        return inv.getItem(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return stack;
        }
        return new InvWrapper(inv).insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return ItemStack.EMPTY;
        }
        return new InvWrapper(inv).extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return 0;
        }
        return new InvWrapper(inv).getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Inventory inv = this.getInventory();
        if (inv == null) {
            return false;
        }
        return new InvWrapper(inv).isItemValid(slot, stack);
    }
}
