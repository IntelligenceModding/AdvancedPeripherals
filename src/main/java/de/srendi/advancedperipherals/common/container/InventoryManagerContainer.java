package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import de.srendi.advancedperipherals.common.container.base.SlotCondition;
import de.srendi.advancedperipherals.common.container.base.SlotInputHandler;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryManagerContainer extends BaseContainer {

    public InventoryManagerContainer(int id, PlayerInventory inventory, BlockPos pos, World world) {
        super(ContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), id, inventory, pos, world);
        layoutPlayerInventorySlots(7, 84);
        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                addSlot(new SlotInputHandler(handler, 0, 79, 29, new SlotCondition().setNeededItem(Items.MEMORY_CARD.get()))); //Input
            });
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
