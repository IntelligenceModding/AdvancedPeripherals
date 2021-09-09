package de.srendi.advancedperipherals.lib.peripherals.owner;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTile;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class TileEntityPeripheralOwner<T extends TileEntity & IPeripheralTileEntity> extends BasePeripheralOwner {

    public final T tileEntity;

    public TileEntityPeripheralOwner(T tileEntity) {
        super();
        this.tileEntity = tileEntity;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return tileEntity.getTileData().getString("CustomName");
    }

    @NotNull
    @Override
    public World getWorld() {
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
        return tileEntity.getBlockState().getValue(APTileEntityBlock.FACING);
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        if (tileEntity instanceof InventoryManagerTile)
            return ((InventoryManagerTile) tileEntity).getOwnerPlayer();
        return null;
    }

    @NotNull
    @Override
    public CompoundNBT getDataStorage() {
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
        World world = getWorld();
        if (world != null)
            getWorld().removeBlock(tileEntity.getBlockPos(), false);
    }

    @Override
    public boolean isMovementPossible(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }

    public TileEntityPeripheralOwner<T> attachFuel() {
        attachAbility(PeripheralOwnerAbility.FUEL, new TileEntityFuelAbility<>(this));
        return this;
    }
}
