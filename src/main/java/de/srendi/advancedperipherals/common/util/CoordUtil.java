package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CoordUtil {

    public static boolean isInRange(@NotNull BlockPos pos, @NotNull Level world, @NotNull Player player, int range) {
        // There are rare cases where these are null. For example if a player detector pocket computer runs while not in a player inventory
        // Fixes https://github.com/SirEndii/AdvancedPeripherals/issues/356
        if(pos == null || world == null || player == null)
            return false;

        range = Math.min(range, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(@NotNull BlockPos pos, @NotNull Level world, @NotNull Player player, int x, int y, int z) {
        if(pos == null || world == null || player == null)
            return false;

        x = Math.min(x * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        y = Math.min(y * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        z = Math.min(z * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(@NotNull BlockPos blockPos, @NotNull Player player, @NotNull Level world, @NotNull BlockPos firstPos, @NotNull BlockPos secondPos) {
        if(blockPos == null || world == null || player == null)
            return false;

        double i = player.getX() - blockPos.getX();
        double j = player.getZ() - blockPos.getZ();
        // Check if the distance of the player is within the max range of the player detector
        if (Math.sqrt(i * i + j * j) > APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get())
            return false;
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(firstPos, secondPos)).contains(player);
    }

    public static Direction getDirection(FrontAndTop orientation, final String computerSide) throws LuaException {
        if (computerSide == null) {
            throw new LuaException("null is not a valid side");
        }

        final String side = computerSide.toLowerCase();
        Direction dir = Direction.byName(side);
        if (dir != null)
            return dir;
        Direction top = orientation.top();
        Direction front = orientation.front();

        if (front.getAxis() == Direction.Axis.Y) {
            if (front == Direction.UP) {
                if (side.equals(ComputerSide.FRONT.toString())) return Direction.UP;
                if (side.equals(ComputerSide.BACK.toString())) return Direction.DOWN;
            } else if (front == Direction.DOWN) {
                if (side.equals(ComputerSide.FRONT.toString())) return Direction.DOWN;
                if (side.equals(ComputerSide.BACK.toString())) return Direction.UP;
            }
            if (side.equals(ComputerSide.TOP.toString())) return top;
            if (side.equals(ComputerSide.BOTTOM.toString())) return top.getOpposite();
            if (side.equals(ComputerSide.RIGHT.toString())) return top.getClockWise();
            if (side.equals(ComputerSide.LEFT.toString())) return top.getCounterClockWise();
        }
        if (side.equals(ComputerSide.FRONT.toString())) return front;
        if (side.equals(ComputerSide.BACK.toString())) return front.getOpposite();
        if (side.equals(ComputerSide.TOP.toString())) return Direction.UP;
        if (side.equals(ComputerSide.BOTTOM.toString())) return Direction.DOWN;
        if (side.equals(ComputerSide.RIGHT.toString())) return front.getCounterClockWise();
        if (side.equals(ComputerSide.LEFT.toString())) return front.getClockWise();

        throw new LuaException(computerSide + " is not a valid side");
    }

}
