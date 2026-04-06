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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
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

    public UUID getOwnerUUID() {
        return this.owner;
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
        if (this.getLevel() == null || this.getLevel().isClientSide()) {
            super.setItem(index, stack);
            return;
        }
        if (stack.getItem() instanceof MemoryCardItem && stack.has(OWNER)) {
            this.owner = stack.remove(OWNER);
        } else {
            this.owner = null;
        }
        super.setItem(index, stack);
        this.sendUpdate();
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag data, @NotNull HolderLookup.Provider provider) {
        this.owner = data.contains("ownerId") ? data.getUUID("ownerId") : null;
        super.loadAdditional(data, provider);
    }

    @Override
    protected void saveShared(@NotNull CompoundTag data, @NotNull HolderLookup.Provider provider) {
        super.saveShared(data, provider);
        if (this.owner != null) {
            data.putUUID("ownerId", this.owner);
        } else {
            // This magic field is required for loadAdditional to run by an update packet,
            // since loadAdditional won't execute when the update tag is empty.
            data.putBoolean("_noOwnerId", true);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ServerPlayer getOwnerPlayer() {
        if (this.owner == null) {
            return null;
        }
        return ((ServerLevel) this.getLevel()).getServer().getPlayerList().getPlayer(this.owner);
    }
}
