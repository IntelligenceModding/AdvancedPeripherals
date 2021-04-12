package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.common.container.base.BaseItemContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class MemoryCardContainer extends BaseItemContainer {

    public MemoryCardContainer(int id, PlayerInventory inventory, ItemStack item) {
        super(id, ContainerTypes.MEMORY_CARD.get(), inventory, item);
        layoutPlayerInventorySlots(7, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.inventory.getCurrentItem().getItem() instanceof MemoryCardItem;
    }
}
