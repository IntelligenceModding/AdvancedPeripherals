package de.srendi.advancedperipherals.common.items.base;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IInventoryItem {
    MenuProvider createContainer(Player player, ItemStack itemStack);

    void writeContainerData(Player player, ItemStack itemStack, RegistryFriendlyByteBuf buf);
}
