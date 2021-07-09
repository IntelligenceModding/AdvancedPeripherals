package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class MechanicSoulPeripheral extends OperationPeripheral {

    public final static int ROTATION_STEPS = 36;

    private int rotationCharge = 0;

    public MechanicSoulPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public MechanicSoulPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public MechanicSoulPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    public MechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    public void addRotationCycle() {
        addRotationCycle(1);
    }

    public void addRotationCycle(int count) {
        if (count > 0)
            rotationCharge += count * ROTATION_STEPS;
    }

    public void consumeRotationCharge() {
        if (rotationCharge > 0)
            rotationCharge -= 1;
    }

    public int getRotationStep() {
        return rotationCharge % ROTATION_STEPS;
    }

    protected Optional<MethodResult> turtleChecks() {
        if (turtle == null) {
            return Optional.of(MethodResult.of(null, "Well, you can use it only from turtle now!"));
        }
        if (turtle.getOwningPlayer() == null) {
            return Optional.of(MethodResult.of(null, "Well, turtle should have owned player!"));
        }
        MinecraftServer server = getWorld().getServer();
        if (server == null) {
            return Optional.of(MethodResult.of(null, "Problem with server finding ..."));
        }
        return Optional.empty();
    }

    protected Pair<MethodResult, TurtleSide> getTurtleSide(@Nonnull IComputerAccess access) {
        TurtleSide side;
        try {
            side = TurtleSide.valueOf(access.getAttachmentName().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Pair.onlyLeft(MethodResult.of(null, e.getMessage()));
        }
        return Pair.onlyRight(side);
    }

    protected Pair<MethodResult, CompoundNBT> getSettings(@Nonnull IComputerAccess access) {
        return getTurtleSide(access).mapRight(side -> turtle.getUpgradeNBTData(side));
    }

    protected Pair<MethodResult, Boolean> setIntSetting(@Nonnull IComputerAccess access, String name, int value) {
        return getSettings(access).mapRight(data -> {
            data.putInt(name, value);
            return true;
        });
    }

    protected Pair<MethodResult, Integer> getIntSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> data.getInt(name));
    }

    protected Pair<MethodResult, CompoundNBT> getCompoundSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> data.getCompound(name));
    }

    protected Pair<MethodResult, Boolean> setCompoundSetting(@Nonnull IComputerAccess access, String name, CompoundNBT value) {
        return getSettings(access).mapRight(data -> {
            data.put(name, value);
            return true;
        });
    }

    protected Pair<MethodResult, Boolean> removeSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> {
            data.remove(name);
            return true;
        });
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }
}
