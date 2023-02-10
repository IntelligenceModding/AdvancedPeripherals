package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.FuelAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.WARP;

public class AutomataWarpingPlugin extends AutomataCorePlugin {

    private static final String POINT_DATA_MARK = "warp_points";
    private static final String WORLD_DATA_MARK = "warp_world";

    public AutomataWarpingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{WARP};
    }

    @NotNull
    protected Pair<MethodResult, CompoundTag> getPointData() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        CompoundTag settings = owner.getDataStorage();
        if (!settings.contains(WORLD_DATA_MARK)) {
            settings.putString(WORLD_DATA_MARK, owner.getLevel().dimension().location().toString());
        } else {
            String worldName = settings.getString(WORLD_DATA_MARK);
            if (!owner.getLevel().dimension().location().toString().equals(worldName))
                return Pair.onlyLeft(MethodResult.of(null, "Incorrect world for this upgrade"));
        }
        if (!settings.contains(POINT_DATA_MARK))
            settings.put(POINT_DATA_MARK, new CompoundTag());

        return Pair.onlyRight(settings.getCompound(POINT_DATA_MARK));
    }

    private int getWarpCost(SingleOperationContext context) {
        FuelAbility<?> fuelAbility = automataCore.getPeripheralOwner().getAbility(PeripheralOwnerAbility.FUEL);
        Objects.requireNonNull(fuelAbility);
        return WARP.getCost(context) * fuelAbility.getFuelConsumptionMultiply();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult savePoint(String name) {
        automataCore.addRotationCycle();
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        CompoundTag data = pairData.getRight();
        if (data.getAllKeys().size() >= APConfig.METAPHYSICS_CONFIG.endAutomataCoreWarpPointLimit.get())
            return MethodResult.of(null, "Cannot add new point, limit reached");

        data.put(name, NBTUtil.toNBT(automataCore.getPeripheralOwner().getPos()));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult deletePoint(String name) {
        automataCore.addRotationCycle();
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        CompoundTag data = pairData.getRight();
        if (!data.contains(name))
            return MethodResult.of(null, "Cannot find point to delete");

        data.remove(name);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult points() {
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        CompoundTag data = pairData.getRight();
        return MethodResult.of(data.getAllKeys());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(String name) throws LuaException {
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        Level level = owner.getLevel();
        CompoundTag data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return automataCore.withOperation(WARP, automataCore.toDistance(newPosition), context -> {
            boolean result = owner.move(level, newPosition);
            if (!result)
                return MethodResult.of(null, "Cannot teleport to location");
            return MethodResult.of(true);
        }, context -> {
            if (!owner.isMovementPossible(level, newPosition))
                return MethodResult.of(null, "Move forbidden");
            return null;
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult estimateWarpCost(String name) {
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        CompoundTag data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(getWarpCost(automataCore.toDistance(newPosition)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult distanceToPoint(String name) {
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent())
            return pairData.getLeft();

        CompoundTag data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(newPosition.distManhattan(automataCore.getPeripheralOwner().getPos()));
    }

}
