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

import java.util.Locale;

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

    public static Direction getDirection(FrontAndTop orientation, String computerSide) throws LuaException {
        if (computerSide == null) {
            throw new LuaException("null is not a valid side");
        }

        computerSide = computerSide.toLowerCase(Locale.ROOT);
        Direction dir = Direction.byName(computerSide);
        if (dir != null)
            return dir;
        Direction top = orientation.top();
        Direction front = orientation.front();

        final ComputerSide side = ComputerSide.valueOfInsensitive(computerSide);
        if (side == null) {
            throw new LuaException(computerSide + " is not a valid side");
        }

        if (front.getAxis() == Direction.Axis.Y) {
            switch (side) {
                case FRONT: return front;
                case BACK: return front.getOpposite();
                case TOP: return top;
                case BOTTOM: return top.getOpposite();
                case RIGHT: return top.getClockWise();
                case LEFT: return top.getCounterClockWise();
            }
        } else {
            switch (side) {
                case FRONT: return front;
                case BACK: return front.getOpposite();
                case TOP: return Direction.UP;
                case BOTTOM: return Direction.DOWN;
                case RIGHT: return front.getCounterClockWise();
                case LEFT: return front.getClockWise();
            }
        }

        throw new LuaException(computerSide + " is not a expected side");
    }

}
