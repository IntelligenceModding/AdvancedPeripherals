package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtlePeripheralOwner extends BasePeripheralOwner {
    public final ITurtleAccess turtle;
    public final TurtleSide side;

    public TurtlePeripheralOwner(ITurtleAccess turtle, TurtleSide side) {
        super();
        this.turtle = turtle;
        this.side = side;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @NotNull
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

    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        return FrontAndTop.fromFrontAndTop(getFacing(), Direction.UP);
    }

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return getOwner();
    }

    @Nullable
    @Override
    public Player getOwner() {
        GameProfile owningPlayer = turtle.getOwningPlayer();
        if (owningPlayer == null) return null;
        return turtle.getLevel().getPlayerByUUID(owningPlayer.getId());
    }

    @Override
    public DataComponentPatch getDataStorage() {
        return DataStorageUtil.getDataStorage(turtle, side);
    }

    @Override
    public CompoundTag getNbtStorage() {
        AdvancedPeripherals.debug("Turtle peripheral at " + getPos() + " tried to use nbt storage but it should instead use data component storage, report to github!", org.apache.logging.log4j.Level.WARN);
        return null;
    }

    @Override
    public void putDataStorage(DataComponentPatch dataStorage) {
        DataStorageUtil.putDataStorage(turtle, side, dataStorage);
    }

    @Override
    public void markDataStorageDirty() {
    }

    @Override
    public <T> T withPlayer(APFakePlayer.Action<T> function) {
        return FakePlayerProviderTurtle.withPlayer(turtle, function);
    }

    @Override
    public ItemStack getToolInMainHand() {
        return turtle.getInventory().getItem(turtle.getSelectedSlot());
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        return InventoryUtil.storeItemsIntoSlot(turtle.getInventory(), stored, turtle.getSelectedSlot());
    }

    @Override
    public void destroyUpgrade() {
        turtle.setUpgrade(side, null);
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return FakePlayerProviderTurtle.withPlayer(turtle, player -> {
            if (level.isOutsideBuildHeight(pos)) return false;
            if (!level.isInWorldBounds(pos)) return false;
            if (!level.isAreaLoaded(pos, 0)) return false;
            return level.getWorldBorder().isWithinBounds(pos);
        });
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return turtle.teleportTo(level, pos);
    }

    @NotNull
    public ITurtleAccess getTurtle() {
        return turtle;
    }

    @NotNull
    public TurtleSide getSide() {
        return side;
    }

    public TurtlePeripheralOwner attachFuel(int maxFuelConsumptionLevel) {
        attachAbility(PeripheralOwnerAbility.FUEL, new TurtleFuelAbility(this, maxFuelConsumptionLevel));
        return this;
    }

    @Override
    public <T extends IPeripheral> T getConnectedPeripheral(Class<T> type) {
        for (TurtleSide side : TurtleSide.values()) {
            IPeripheral peripheral = turtle.getPeripheral(side);
            if (peripheral == null || !type.isInstance(peripheral)) {
                continue;
            }
            if (peripheral instanceof IBasePeripheral basePeripheral && !basePeripheral.isEnabled()) {
                continue;
            }
            return (T) peripheral;
        }
        return null;
    }
}
