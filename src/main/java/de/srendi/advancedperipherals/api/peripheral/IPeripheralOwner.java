package de.srendi.advancedperipherals.api.peripheral;

import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public interface IPeripheralOwner {
    @Nullable String getCustomName();
    @Nullable World getWorld();
    @Nonnull BlockPos getPos();
    @Nonnull Direction getFacing();
    @Nullable PlayerEntity getOwner();
    @Nonnull CompoundNBT getSettings();
    int getFuelCount();
    int getFuelMaxCount();
    boolean consumeFuel(int count, boolean simulate);
    void addFuel(int count);

    <T> T withPlayer(Function<APFakePlayer, T> function);
    ItemStack getToolInMainHand();
    ItemStack storeItem(ItemStack stored);
    void destroyUpgrade();
    boolean isMovementPossible(@Nonnull World world, @Nonnull BlockPos pos);
    boolean move(@Nonnull World world, @Nonnull BlockPos pos);

    // Strange methods, that shouldn't exists ...
    void triggerClientServerSync();
}
