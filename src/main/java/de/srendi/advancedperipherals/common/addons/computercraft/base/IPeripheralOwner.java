package de.srendi.advancedperipherals.common.addons.computercraft.base;

import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public interface IPeripheralOwner {
    @Nullable
    String getCustomName();

    @Nullable
    Level getLevel();

    @Nonnull
    BlockPos getPos();

    @Nonnull
    Direction getFacing();

    @Nullable
    Player getOwner();

    @Nonnull
    CompoundTag getDataStorage();

    void markDataStorageDirty();

    int getFuelCount();

    int getFuelMaxCount();

    boolean consumeFuel(int count, boolean simulate);

    void addFuel(int count);

    <T> T withPlayer(Function<APFakePlayer, T> function);

    ItemStack getToolInMainHand();

    ItemStack storeItem(ItemStack stored);

    void destroyUpgrade();

    boolean isMovementPossible(@Nonnull Level level, @Nonnull BlockPos pos);

    boolean move(@Nonnull Level level, @Nonnull BlockPos pos);
}
