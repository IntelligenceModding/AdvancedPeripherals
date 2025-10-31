package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
        return pocket.getLevel();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        Vec3 position = pocket.getPosition();
        return new BlockPos((int) position.x, (int) position.y, (int) position.z);
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

    @Override
    public DataComponentPatch getDataStorage() {
        return DataStorageUtil.getDataStorage(pocket);
    }

    @Override
    public CompoundTag getNbtStorage() {
        AdvancedPeripherals.debug("Pocket peripheral at " + getPos() + " tried to use nbt storage but it should instead use data component storage, report to github!", org.apache.logging.log4j.Level.WARN);
        return null;
    }

    @Override
    public void putDataStorage(DataComponentPatch dataStorage) {
        DataStorageUtil.putDataStorage(pocket, dataStorage);
    }

    @Override
    public void markDataStorageDirty() {
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
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }
}
