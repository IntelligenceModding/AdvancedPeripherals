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
    Level getWorld();

    @Nonnull
    BlockPos getPos();

    @Nonnull
    Direction getFacing();

    @Nullable
    Player getOwner();

    @Nonnull
    CompoundTag getDataStorage();

    int getFuelCount();

    int getFuelMaxCount();

    boolean consumeFuel(int count, boolean simulate);

    void addFuel(int count);

    <T> T withPlayer(Function<APFakePlayer, T> function);

    ItemStack getToolInMainHand();

    ItemStack storeItem(ItemStack stored);

    void destroyUpgrade();

    boolean isMovementPossible(@Nonnull Level world, @Nonnull BlockPos pos);

    boolean move(@Nonnull Level world, @Nonnull BlockPos pos);

    // Strange methods, that shouldn't exists ...
    void triggerClientServerSync();
}
