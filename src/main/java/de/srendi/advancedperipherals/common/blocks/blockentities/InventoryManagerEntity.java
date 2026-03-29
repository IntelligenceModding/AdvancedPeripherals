package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.IInventoryMenuBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.OWNER;

public class InventoryManagerEntity extends PeripheralBlockEntity<InventoryManagerPeripheral> implements IInventoryMenuBlock {

    private UUID owner = null;

    public InventoryManagerEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.INVENTORY_MANAGER.get(), pos, state);
    }

    @Override
    @NotNull
    protected InventoryManagerPeripheral buildPeripheral() {
        return new InventoryManagerPeripheral(this);
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return Component.translatable("block.advancedperipherals.inventory_manager");
    }

    @Override
    protected InventoryManagerContainer createMenu(int id, Inventory playerInventory) {
        return new InventoryManagerContainer(id, playerInventory, this.getBlockPos(), this.getLevel());
    }

    @Override
    public int getInvSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
        return itemStackIn.getItem() instanceof MemoryCardItem;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        boolean shouldClearOwner = false;
        if (stack.getItem() instanceof MemoryCardItem) {
            if (stack.has(OWNER)) {
                this.owner = stack.get(OWNER);
                stack.remove(OWNER);
            } else if (stack != this.getItem(index)) {
                // Only clear owner when the new card item is not the current item
                shouldClearOwner = true;
            }
        } else {
            shouldClearOwner = true;
        }
        if (shouldClearOwner && this.getLevel() != null && !this.getLevel().isClientSide()) {
            this.owner = null;
        }
        super.setItem(index, stack);
    }

    @Override
    public void loadAdditional(CompoundTag data, @NotNull HolderLookup.Provider provider) {
        if (data.contains("ownerId")) {
            this.owner = data.getUUID("ownerId");
        }
        super.loadAdditional(data, provider);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag data, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(data, provider);
        if (this.owner != null) {
            data.putUUID("ownerId", this.owner);
        }
    }

    public Player getOwnerPlayer() {
        if (this.owner == null) {
            return null;
        }
        return ((ServerLevel) this.getLevel()).getServer().getPlayerList().getPlayer(this.owner);
    }
}
