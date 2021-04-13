package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.common.container.base.BaseItemContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MemoryCardContainer extends BaseItemContainer {

    ItemStack item;

    public MemoryCardContainer(int id, PlayerInventory inventory, ItemStack item) {
        super(id, ContainerTypes.MEMORY_CARD.get(), inventory, item);
        this.item = item;
        layoutPlayerInventorySlots(7, 84);
    }

    public MemoryCardContainer(int id, PlayerInventory inventory, PacketBuffer buf) {
        super(id, ContainerTypes.MEMORY_CARD.get(), inventory, buf.readItemStack());
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.inventory.getCurrentItem().getItem() instanceof MemoryCardItem;
    }
}
