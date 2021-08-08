package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IAutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.IPeripheralOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.WARP;


public class EndAutomataCorePeripheral extends WeakAutomataCorePeripheral {

    private final static String POINT_DATA_MARK = "warp_points";
    private final static String WORLD_DATA_MARK = "warp_world";

    public EndAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEndAutomataCore;
    }

    @Override
    public IAutomataCoreTier getTier() {
        return AutomataCoreTier.TIER2;
    }

    protected @Nonnull
    Pair<MethodResult, CompoundNBT> getPointData() {
        CompoundNBT settings = owner.getDataStorage();
        if (!settings.contains(WORLD_DATA_MARK)) {
            settings.putString(WORLD_DATA_MARK, getWorld().dimension().location().toString());
        } else {
            String worldName = settings.getString(WORLD_DATA_MARK);
            if (!getWorld().dimension().location().toString().equals(worldName)) {
                return Pair.onlyLeft(MethodResult.of(null, "Incorrect world for this upgrade"));
            }
        }
        if (!settings.contains(POINT_DATA_MARK)) {
            settings.put(POINT_DATA_MARK, new CompoundNBT());
        }
        return Pair.onlyRight(settings.getCompound(POINT_DATA_MARK));
    }

    private int getWarpCost(SingleOperationContext context) {
        return WARP.getCost(context) * fuelConsumptionMultiply();
    }

    @Override
    public List<IPeripheralOperation<?>> possibleOperations() {
        List<IPeripheralOperation<?>> data = super.possibleOperations();
        data.add(WARP);
        return data;
    }

    @LuaFunction
    public final MethodResult savePoint(String name) {
        addRotationCycle();
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        if (data.getAllKeys().size() >= AdvancedPeripheralsConfig.endAutomataCoreWarpPointLimit)
            return MethodResult.of(null, "Cannot add new point, limit reached");
        data.put(name, NBTUtil.toNBT(getPos()));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult deletePoint(String name) {
        addRotationCycle();
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        if (!data.contains(name))
            return MethodResult.of(null, "Cannot find point to delete");
        data.remove(name);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult points() {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        return MethodResult.of(data.getAllKeys());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        Optional<MethodResult> checkResults = cooldownCheck(WARP);
        if (checkResults.isPresent()) return checkResults.get();
        World world = getWorld();
        addRotationCycle();
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        if (!owner.isMovementPossible(world, newPosition))
            return MethodResult.of(null, "Move forbidden");
        SingleOperationContext context = toDistance(newPosition);
        int warpCost = getWarpCost(context);
        if (consumeFuel(warpCost, true)) {
            boolean teleportResult = owner.move(world, newPosition);
            if (teleportResult) {
                consumeFuel(warpCost, false);
                return MethodResult.of(true);
            } else {
                return MethodResult.of(null, "Cannot teleport to location");
            }
        }
        trackOperation(WARP, context);
        return MethodResult.of(null, String.format("Not enough fuel, %d needed", warpCost));
    }

    @LuaFunction
    public final MethodResult estimateWarpCost(String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(getWarpCost(toDistance(newPosition)));
    }

    @LuaFunction
    public final MethodResult distanceToPoint(String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(newPosition.distManhattan(getPos()));
    }

}
