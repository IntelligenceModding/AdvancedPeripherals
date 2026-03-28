package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BasePeripheralOwner;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.stream.Stream;

public class ModulePeripheralOwner extends BasePeripheralOwner {
    private final SmartGlassesComputer computer;

    public ModulePeripheralOwner(SmartGlassesComputer computer) {
        this.computer = computer;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return computer.getEntity().level();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return BlockPos.containing(getCenterPos());
    }

    @NotNull
    @Override
    public Vec3 getCenterPos() {
        return computer.getEntity().getEyePosition();
    }

    @NotNull
    @Override
    public Vec3 getDirection() {
        return computer.getEntity().getLookAngle();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return Direction.getNearest(this.getDirection());
    }

    @NotNull
    @Override
    public FrontAndTop getFrontAndTop() {
        Vec3 up = computer.getEntity().getUpVector(1.0f);
        return FrontAndTop.fromFrontAndTop(getFacing(), Direction.getNearest(up));
    }

    @NotNull
    @Override
    public Quaterniondc getOrientation() {
        Entity owner = computer.getEntity();
        Vec3 front = owner.getLookAngle();
        Vec3 up = owner.getUpVector(1.0f);
        Vec3 right = front.cross(up);
        return new Quaterniond().setFromNormalized(new Matrix3d(
            right.x, up.x, -front.x,
            right.y, up.y, -front.y,
            right.z, up.z, -front.z
        ));
    }

    @NotNull
    public SmartGlassesComputer getComputer() {
        return computer;
    }

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return computer.getEntity();
    }

    @Nullable
    @Override
    public Player getOwner() {
        Entity owner = computer.getEntity();
        return owner instanceof Player player ? player : null;
    }

    @NotNull
    @Override
    public DataComponentPatch getDataStorage() {
        return computer.getModulesData();
    }

    @Override
    public void putDataStorage(DataComponentPatch patch) {
        computer.setModulesData(patch);
    }

    @Override
    public <T> T withPlayer(APFakePlayer.Action<T> function) throws LuaException {
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

    @Override
    public <T extends IPeripheral> T getConnectedPeripheral(Class<T> type) {
        IPeripheral foundPeripheral = Stream.of(ComputerSide.values())
            .map(side -> computer.getPeripheral(side))
            .filter(peripheral -> {
                if (peripheral == null || type.isInstance(peripheral)) {
                    return false;
                }
                return peripheral instanceof IBasePeripheral basePeripheral ? basePeripheral.isEnabled() : true;
            })
            .findFirst()
            .orElse(null);
        return (T) foundPeripheral;
    }
}
