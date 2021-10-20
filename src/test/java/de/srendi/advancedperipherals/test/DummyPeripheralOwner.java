package de.srendi.advancedperipherals.test;

import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.owner.BasePeripheralOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DummyPeripheralOwner extends BasePeripheralOwner {

    private final CompoundNBT dataStorage = new CompoundNBT();

    @Override
    @Nullable
    public String getCustomName() {
        return null;
    }

    @Override
    @Nullable
    public World getWorld() {
        return null;
    }

    @Override
    @NotNull
    public BlockPos getPos() {
        return new BlockPos(0, 0, 0);
    }

    @Override
    @NotNull
    public Direction getFacing() {
        return Direction.NORTH;
    }

    @Override
    @Nullable
    public PlayerEntity getOwner() {
        return null;
    }

    @Override
    @NotNull
    public CompoundNBT getDataStorage() {
        return dataStorage;
    }

    @Override
    public void markDataStorageDirty() {

    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        return stored;
    }

    @Override
    public void destroyUpgrade() {

    }

    @Override
    public boolean isMovementPossible(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }
}
