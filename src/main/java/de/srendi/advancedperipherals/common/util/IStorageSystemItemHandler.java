package de.srendi.advancedperipherals.common.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IStorageSystemItemHandler extends IItemHandler {

    ItemStack extractItem(ItemFilter filter, boolean simulate);

}
