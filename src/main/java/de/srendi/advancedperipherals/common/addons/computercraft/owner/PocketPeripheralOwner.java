package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PocketPeripheralOwner extends BasePeripheralOwner {
    private final IPocketAccess pocket;

    public PocketPeripheralOwner(IPocketAccess pocket) {
        super();
        this.pocket = pocket;
        if(APConfig.PERIPHERALS_CONFIG.disablePocketFuelConsumption.get())
            attachAbility(PeripheralOwnerAbility.FUEL, new InfinitePocketFuelAbility(this));
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        Entity owner = pocket.getEntity();
        if (owner == null) return null;
        return owner.getCommandSenderWorld();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        Entity owner = pocket.getEntity();
        if (owner == null) return new BlockPos(0, 0, 0);
        return owner.blockPosition();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        Entity owner = pocket.getEntity();
        if (owner == null) return Direction.NORTH;
        return owner.getDirection();
    }


    /**
     * Not used for pockets
     */
    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        return FrontAndTop.NORTH_UP;
    }

    @Nullable
    @Override
    public Player getOwner() {
        Entity owner = pocket.getEntity();
        if (owner instanceof Player player) return player;
        return null;
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(pocket);
    }

    @Override
    public void markDataStorageDirty() {
        pocket.updateUpgradeNBTData();
    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        throw new NotImplementedException();
    }

    @Override
    public void destroyUpgrade() {
        throw new NotImplementedException();
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
