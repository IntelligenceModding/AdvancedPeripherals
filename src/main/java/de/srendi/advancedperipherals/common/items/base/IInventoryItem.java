package de.srendi.advancedperipherals.common.items.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;

public interface IInventoryItem {

    INamedContainerProvider createContainer(PlayerEntity playerEntity, ItemStack itemStack);

}
