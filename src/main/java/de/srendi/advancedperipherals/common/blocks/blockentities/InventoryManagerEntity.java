package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.IInventoryBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InventoryManagerEntity extends PeripheralBlockEntity<InventoryManagerPeripheral> implements IInventoryBlock<InventoryManagerContainer> {

    private UUID owner = null;

    public InventoryManagerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.INVENTORY_MANAGER.get(), pos, state);
    }

    @NotNull
    @Override
    protected InventoryManagerPeripheral createPeripheral() {
        return new InventoryManagerPeripheral(this);
    }

    @Override
    public InventoryManagerContainer createContainer(int id, Inventory playerInventory, BlockPos pos, Level world) {
        return new InventoryManagerContainer(id, playerInventory, pos, world);
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
            if (stack.hasTag() && stack.getTag().contains("ownerId")) {
                UUID owner = stack.getTag().getUUID("ownerId");
                this.owner = owner;
                stack.getTag().remove("ownerId");
                stack.getTag().remove("owner");
            } else if (stack != this.getItem(index)) {
                // Only clear owner when the new card item is not the current item
                shouldClearOwner = true;
            }
        } else {
            shouldClearOwner = true;
        }
        if (shouldClearOwner && this.getLevel() != null && !this.getLevel().isClientSide) {
            this.owner = null;
        }
        super.setItem(index, stack);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.advancedperipherals.inventory_manager");
    }

    @Override
    public void load(@NotNull CompoundTag data) {
        if (data.contains("ownerId")) {
            this.owner = data.getUUID("ownerId");
        }
        super.load(data);
    }

    @Override
    protected void saveShared(@NotNull CompoundTag data) {
        super.saveShared(data);
        if (this.owner != null) {
            data.putUUID("ownerId", this.owner);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Fresh the memory card for backward compatibility
        // TODO: remove in 0.8
        if (!this.getLevel().isClientSide) {
            this.setItem(0, this.getItem(0));
        }
    }

    public Player getOwnerPlayer() {
        if (this.owner == null) {
            return null;
        }
        Player player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(this.owner);
        return player;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
