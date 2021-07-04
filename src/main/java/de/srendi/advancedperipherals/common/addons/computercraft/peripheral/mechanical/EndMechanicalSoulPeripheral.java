package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanical;

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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EndMechanicalSoulPeripheral extends WeakMechanicSoulPeripheral {

    private final static String POINT_DATA_MARK = "warp_points";
    private final static String WORLD_DATA_MARK = "warp_world";

    public EndMechanicalSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEndMechanicalSoul;
    }

    public int getItemSuckRadius() {
        return AdvancedPeripheralsConfig.endMechanicalSoulSuckRange;
    }

    private Pair<MethodResult, CompoundNBT> getPointData(IComputerAccess access){
        TurtleSide side;
        try {
            side = TurtleSide.valueOf(access.getAttachmentName().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Pair.onlyLeft(MethodResult.of(null, e.getMessage()));
        }
        CompoundNBT upgradeData = turtle.getUpgradeNBTData(side);
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
        return Pair.onlyRight(turtle.getUpgradeNBTData(side).getCompound(POINT_DATA_MARK));
    }

    private int getWarpCost(BlockPos warpTarget) {
        return (int) Math.sqrt(warpTarget.distManhattan(getPos()));
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
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, getPos(), turtle.getDirection());
        TurtleBlockEvent.Move moveEvent = new TurtleBlockEvent.Move(turtle, turtlePlayer, getWorld(), newPosition);
        if(MinecraftForge.EVENT_BUS.post(moveEvent))
            return MethodResult.of(null, "Move forbidden");
        int warpCost = getWarpCost(newPosition);
        if (consumeFuel(warpCost, true)) {
            boolean teleportResult = turtle.teleportTo(getWorld(), newPosition);
            if (teleportResult) {
                consumeFuel(warpCost, false);
                return MethodResult.of(true);
            } else {
                return MethodResult.of(null, "Cannot teleport to location");
            }
        }
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
        return MethodResult.of(getWarpCost(newPosition));
    }

    @LuaFunction
    public final MethodResult distanceToPoint(@NotNull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData(access);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(newPosition.distManhattan(getPos()));
    }

}
