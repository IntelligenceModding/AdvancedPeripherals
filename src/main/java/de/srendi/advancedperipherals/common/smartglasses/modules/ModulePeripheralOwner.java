package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.common.addons.computercraft.owner.BasePeripheralOwner;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
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

public class ModulePeripheralOwner extends BasePeripheralOwner {

    //TODO: Think about making our own smart glasses access so we don't have the not used stuff like the color or the light
    // We would need to remove the pocket stuff from the SmartGlassesComputer
    private final SmartGlassesComputer computer;

    public ModulePeripheralOwner(SmartGlassesComputer computer) {
        this.computer = computer;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return "smartglasses";
    }

    @Nullable
    @Override
    public Level getLevel() {
        return computer.getEntity().getLevel();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return computer.getEntity().getOnPos();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return Direction.NORTH;
    }

    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        return FrontAndTop.NORTH_UP;
    }

    @NotNull
    public SmartGlassesComputer getComputer() {
        return computer;
    }

    @Nullable
    @Override
    public Player getOwner() {
        Entity owner = computer.getEntity();
        if (owner instanceof Player player) return player;
        return null;
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return computer.getUpgradeNBTData();
    }

    @Override
    public void markDataStorageDirty() {
        computer.updateUpgradeNBTData();
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
