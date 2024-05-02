package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class BlockEntityPeripheralOwner<T extends BlockEntity & IPeripheralTileEntity> extends BasePeripheralOwner {

    public final T tileEntity;

    public BlockEntityPeripheralOwner(T tileEntity) {
        super();
        this.tileEntity = tileEntity;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return tileEntity.getPersistentData().getString("CustomName");
    }

    @NotNull
    @Override
    public Level getLevel() {
        return Objects.requireNonNull(tileEntity.getLevel());
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return tileEntity.getBlockPos();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return tileEntity.getBlockState().getValue(JigsawBlock.ORIENTATION).front();
    }

    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        return tileEntity.getBlockState().getValue(BaseBlock.ORIENTATION);
    }

    @Nullable
    @Override
    public Player getOwner() {
        if (tileEntity instanceof InventoryManagerEntity inventoryManagerEntity)
            return inventoryManagerEntity.getOwnerPlayer();
        return null;
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(tileEntity);
    }

    @Override
    public void markDataStorageDirty() {
        tileEntity.setChanged();
    }

    @Override
    public <T1> T1 withPlayer(Function<APFakePlayer, T1> function) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // TODO: tricks with capability needed
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void destroyUpgrade() {
        getLevel().removeBlock(tileEntity.getBlockPos(), false);
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    public BlockEntityPeripheralOwner<T> attachFuel() {
        attachAbility(PeripheralOwnerAbility.FUEL, new TileEntityFuelAbility<>(this));
        return this;
    }
}
