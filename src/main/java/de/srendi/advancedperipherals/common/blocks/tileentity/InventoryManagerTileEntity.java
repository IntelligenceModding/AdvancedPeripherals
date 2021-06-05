package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.IInventoryBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class InventoryManagerTileEntity extends PeripheralTileEntity<InventoryManagerPeripheral> implements IInventoryBlock<InventoryManagerContainer> {

    public InventoryManagerTileEntity() {
        super(TileEntityTypes.INVENTORY_MANAGER.get());
    }

    @Override
    protected InventoryManagerPeripheral createPeripheral() {
        return new InventoryManagerPeripheral("inventoryManager", this);
    }

    @Override
    public InventoryManagerContainer createContainer(int id, PlayerInventory playerInventory, BlockPos pos, World world) {
        return new InventoryManagerContainer(id, playerInventory, pos, world);
    }

    @Override
    public int getInvSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return itemStackIn.getItem() instanceof MemoryCardItem;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.advancedperipherals.inventory_manager");
    }

    public PlayerEntity getOwnerPlayer() {
        //Checks if the tile entity has an item in his inventory
        if (items.get(0).isEmpty())
            return null;
        ItemStack stack = items.get(0);
        //Checks if the item contains the owner name
        if (!stack.getOrCreateTag().contains("owner"))
            return null;
        //Loop through all players and check if the player is online
        for (PlayerEntity entity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (entity.getName().getString().equals(stack.getOrCreateTag().getString("owner")))
                return entity;
        }
        return null;
    }
}
