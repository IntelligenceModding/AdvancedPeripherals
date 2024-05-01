package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

/**
 * Used to assign a container to a TileEntity
 *
 * @param <T> The container related to this inventory
 */

public interface IInventoryBlock<T extends BaseContainer> {

    Component getDisplayName();

    T createContainer(int id, Inventory playerInventory, BlockPos pos, Level world);

    int getInvSize();
}
