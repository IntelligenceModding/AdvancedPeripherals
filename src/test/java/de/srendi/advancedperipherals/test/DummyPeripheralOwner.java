package de.srendi.advancedperipherals.test;

import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.owner.BasePeripheralOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DummyPeripheralOwner extends BasePeripheralOwner {

    private final CompoundTag dataStorage = new CompoundTag();

    @Override
    public @Nullable String getCustomName() {
        return null;
    }

    @Override
    public @Nullable
    Level getLevel() {
        return null;
    }

    @Override
    public @NotNull
    BlockPos getPos() {
        return new BlockPos(0, 0, 0);
    }

    @Override
    public @NotNull
    Direction getFacing() {
        return Direction.NORTH;
    }

    @Override
    public @Nullable
    Player getOwner() {
        return null;
    }

    @Override
    public @NotNull CompoundTag getDataStorage() {
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
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }
}