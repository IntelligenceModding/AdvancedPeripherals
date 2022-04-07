package de.srendi.advancedperipherals.common.container.base;

import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainer extends AbstractContainerMenu {

    private final IItemHandler inventory;
    protected PeripheralBlockEntity<?> tileEntity;

    protected BaseContainer(@Nullable MenuType<?> type, int id, Inventory inventory, BlockPos pos, Level world) {
        super(type, id);
        this.inventory = new InvWrapper(inventory);
        if (world != null)
            this.tileEntity = (PeripheralBlockEntity<?>) world.getBlockEntity(pos);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= 36) {
                if (!this.moveItemStackTo(itemstack, 0, 36, false) && !tileEntity.canTakeItemThroughFace(index, itemstack, Direction.NORTH)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack, itemstack1);
            } else {
                if (!this.moveItemStackTo(itemstack, 36, getItems().size(), false) && !tileEntity.canPlaceItemThroughFace(index, itemstack, Direction.NORTH)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(playerIn, itemstack);
        }

        return itemstack;
    }


    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int i = 0; i < verAmount; i++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

}
