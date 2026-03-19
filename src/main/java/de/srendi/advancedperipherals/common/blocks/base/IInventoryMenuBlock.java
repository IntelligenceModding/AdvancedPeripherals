package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;

/**
 * Used to assign an inventory storage to a block entity with a proper container
 * @param <T> the container for that block entity
 */
public interface IInventoryMenuBlock extends IInventoryBlock, MenuProvider {
}
