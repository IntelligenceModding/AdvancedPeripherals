package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import de.srendi.advancedperipherals.common.container.base.SlotCondition;
import de.srendi.advancedperipherals.common.container.base.SlotInputHandler;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.setup.APItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryManagerContainer extends BaseContainer {

    public static final int SLOT_X = 79, SLOT_Y = 29;

    public InventoryManagerContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(APContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), id, inventory, pos, level);
        layoutPlayerInventorySlots(7, 84);
        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (handler != null) {
                addSlot(new SlotInputHandler(handler, 0, SLOT_X, SLOT_Y, new SlotCondition().setNeededItem(APItems.MEMORY_CARD.get())));
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= 36) {
                if (!this.moveItemStackTo(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index <= 35) {
                if (itemstack1.getItem().equals(APItems.MEMORY_CARD.get())) {
                    if (!this.moveItemStackTo(itemstack1, 36, 37, true)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

}
