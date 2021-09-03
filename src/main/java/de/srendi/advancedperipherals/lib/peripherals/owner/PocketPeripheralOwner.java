package de.srendi.advancedperipherals.lib.peripherals.owner;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PocketPeripheralOwner extends BasePeripheralOwner {
    private final IPocketAccess pocket;

    public PocketPeripheralOwner(IPocketAccess pocket) {
        super();
        this.pocket = pocket;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public World getWorld() {
        Entity owner = pocket.getEntity();
        if (owner == null)
            return null;
        return owner.getCommandSenderWorld();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        Entity owner = pocket.getEntity();
        if (owner == null)
            return new BlockPos(0, 0, 0);
        return owner.blockPosition();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        Entity owner = pocket.getEntity();
        if (owner == null)
            return Direction.NORTH;
        return owner.getDirection();
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        Entity owner = pocket.getEntity();
        if (owner instanceof PlayerEntity)
            return (PlayerEntity) owner;
        return null;
    }

    @NotNull
    @Override
    public CompoundNBT getDataStorage() {
        return DataStorageUtil.getDataStorage(pocket);
    }

    @Override
    public void markDataStorageDirty() {
        pocket.updateUpgradeNBTData();
    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // Tricks with inventory needed
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void destroyUpgrade() {
        throw new RuntimeException("Not implemented yet");
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
