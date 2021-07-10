package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class MechanicSoulPeripheral extends FuelConsumingPeripheral {

    public final static int ROTATION_STEPS = 36;
    public final static String ROTATION_CHARGE_SETTING = "rotationCharge";

    public MechanicSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public void addRotationCycle() {
        addRotationCycle(1);
    }

    public void addRotationCycle(int count) {
        System.out.printf("Adding rotation cycle %d with turtle access%n", count);
        CompoundNBT data = owner.getSettings();
        data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING)) + count * ROTATION_STEPS);
        owner.triggerClientServerSync();
    }

    public void consumeRotationCharge() {
        CompoundNBT data = owner.getSettings();
        int currentCharge = data.getInt(ROTATION_CHARGE_SETTING);
        if (currentCharge > 0) {
            System.out.printf("Consume rotation charge, was %d%n", data.getInt(ROTATION_CHARGE_SETTING));
            data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING) - 1));
            System.out.printf("Consume rotation charge, left %d%n", data.getInt(ROTATION_CHARGE_SETTING));
        }
    }

    public int getRotationStep() {
        int rotation = owner.getSettings().getInt(ROTATION_CHARGE_SETTING);
        if (rotation > 0)
            System.out.printf("Current charge %d%n", rotation);
        return rotation;
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }
}
