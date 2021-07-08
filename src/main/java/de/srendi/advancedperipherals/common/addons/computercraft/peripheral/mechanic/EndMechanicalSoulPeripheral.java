package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.event.TurtleBlockEvent;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;


public class EndMechanicalSoulPeripheral extends WeakMechanicSoulPeripheral {

    private final static String POINT_DATA_MARK = "warp_points";
    private final static String WORLD_DATA_MARK = "warp_world";
    private final static String WARP_OPERATION = "warp";

    public EndMechanicalSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEndMechanicSoul;
    }

    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.endMechanicSoulInteractionRadius;
    }

    @Override
    protected int getRawCooldown(String name) {
        if (name.equals(WARP_OPERATION))
            return AdvancedPeripheralsConfig.warpCooldown;
        return super.getRawCooldown(name);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.endMechanicSoulMaxFuelConsumptionLevel;
    }

    protected @Nonnull Pair<MethodResult, CompoundNBT> getPointData(@Nonnull IComputerAccess access) {
        Pair<MethodResult, TurtleSide> sideResult = getTurtleSide(access);
        if (sideResult.leftPresent())
            return sideResult.ignoreRight();
        CompoundNBT upgradeData = turtle.getUpgradeNBTData(sideResult.getRight());
        if (!upgradeData.contains(WORLD_DATA_MARK)) {
            upgradeData.putString(WORLD_DATA_MARK, getWorld().dimension().location().toString());
        } else {
            String worldName = upgradeData.getString(WORLD_DATA_MARK);
            if (!getWorld().dimension().location().toString().equals(worldName)) {
                return Pair.onlyLeft(MethodResult.of(null, "Incorrect world for this upgrade"));
            }
        }
        if (!upgradeData.contains(POINT_DATA_MARK)) {
            upgradeData.put(POINT_DATA_MARK, new CompoundNBT());
        }
        return Pair.onlyRight(turtle.getUpgradeNBTData(sideResult.getRight()).getCompound(POINT_DATA_MARK));
    }

    private int getWarpCost(@Nonnull IComputerAccess access, BlockPos warpTarget) {
        return (int) Math.sqrt(warpTarget.distManhattan(getPos())) * fuelConsumptionMultiply(access);
    }

    @LuaFunction
    public Map<String, Object> getConfiguration() {
        Map<String, Object> result = super.getConfiguration();
        result.put("warpCooldown", AdvancedPeripheralsConfig.warpCooldown);
        return result;
    }

    @LuaFunction
    public int getWarpCooldown() {
        return getCurrentCooldown(WARP_OPERATION);
    }

    @LuaFunction
    public final MethodResult savePoint(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        data.put(name, NBTUtil.toNBT(getPos()));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult points(@Nonnull IComputerAccess access) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        return MethodResult.of(data.getAllKeys());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        Optional<MethodResult> checkResults = cooldownCheck(WARP_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, getPos(), turtle.getDirection());
        TurtleBlockEvent.Move moveEvent = new TurtleBlockEvent.Move(turtle, turtlePlayer, getWorld(), newPosition);
        if (MinecraftForge.EVENT_BUS.post(moveEvent))
            return MethodResult.of(null, "Move forbidden");
        int warpCost = getWarpCost(access, newPosition);
        if (consumeFuel(access, warpCost, true)) {
            boolean teleportResult = turtle.teleportTo(getWorld(), newPosition);
            if (teleportResult) {
                consumeFuel(access, warpCost, false);
                return MethodResult.of(true);
            } else {
                return MethodResult.of(null, "Cannot teleport to location");
            }
        }
        trackOperation(access, WARP_OPERATION);
        return MethodResult.of(null, String.format("Not enough fuel, %d needed", warpCost));
    }

    @LuaFunction
    public final MethodResult estimateWarpCost(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(getWarpCost(access, newPosition));
    }

    @LuaFunction
    public final MethodResult distanceToPoint(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(newPosition.distManhattan(getPos()));
    }

}
