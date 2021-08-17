package de.srendi.advancedperipherals.common.addons.computercraft.base;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.event.TurtleBlockEvent;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TurtlePeripheralOwner implements IPeripheralOwner {
    private final ITurtleAccess turtle;
    private final TurtleSide side;

    public TurtlePeripheralOwner(ITurtleAccess turtle, TurtleSide side) {
        this.turtle = turtle;
        this.side = side;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return turtle.getLevel();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return turtle.getPosition();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return turtle.getDirection();
    }

    @Nullable
    @Override
    public Player getOwner() {
        GameProfile owningPlayer = turtle.getOwningPlayer();
        if (owningPlayer == null)
            return null;
        return turtle.getLevel().getPlayerByUUID(owningPlayer.getId());
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(turtle, side);
    }

    @Override
    public void markDataStorageDirty() {
        turtle.updateUpgradeNBTData(side);
    }

    @Override
    public int getFuelCount() {
        return turtle.getFuelLevel();
    }

    @Override
    public int getFuelMaxCount() {
        return turtle.getFuelLimit();
    }

    @Override
    public boolean consumeFuel(int count, boolean simulate) {
        if (simulate)
            return turtle.getFuelLevel() >= count;
        return turtle.consumeFuel(count);
    }

    @Override
    public void addFuel(int count) {
        turtle.addFuel(count);
    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        return FakePlayerProviderTurtle.withPlayer(turtle, function);
    }

    @Override
    public ItemStack getToolInMainHand() {
        return turtle.getInventory().getItem(turtle.getSelectedSlot());
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        return InventoryUtil.storeItems(stored, turtle.getItemHandler(), turtle.getSelectedSlot());
    }

    @Override
    public void destroyUpgrade() {
        turtle.setUpgrade(side, null);
    }

    @Override
    public boolean isMovementPossible(@Nonnull Level level, @Nonnull BlockPos pos) {
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, getPos(), turtle.getDirection());
        TurtleBlockEvent.Move moveEvent = new TurtleBlockEvent.Move(turtle, turtlePlayer, level, pos);
        return !MinecraftForge.EVENT_BUS.post(moveEvent);
    }

    @Override
    public boolean move(@Nonnull Level level, @Nonnull BlockPos pos) {
        return turtle.teleportTo(level, pos);
    }
}
