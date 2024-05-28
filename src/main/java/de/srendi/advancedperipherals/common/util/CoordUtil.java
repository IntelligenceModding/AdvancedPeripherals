package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CoordUtil {

    public static boolean isInRange(@Nullable BlockPos pos, @Nullable Level world, @Nullable Player player, int range, int maxRange) {
        // There are rare cases where these can be null. For example if a player detector pocket computer runs while not in a player inventory
        // Fixes https://github.com/SirEndii/AdvancedPeripherals/issues/356
        if (pos == null || world == null || player == null)
            return false;

        range = maxRange == -1 ? range : Math.min(Math.abs(range), maxRange);
        return isPlayerInBlockRange(pos, world, player, (double) range);
    }

    // To fix issue #439
    private static boolean isPlayerInBlockRange(@NotNull BlockPos pos, @NotNull Level world, @NotNull Player player, double range) {
        if (range != -1 && player.getLevel() != world)
            return false;

        double x = player.getX(), y = player.getY(), ey = player.getEyeY(), z = player.getZ();
        if (ey > y) { // Ensure following code will work when eye position is lower than feet position
            double tmp = ey;
            ey = y;
            y = tmp;
        }
        double bx = (double)(pos.getX() + 0.5), by = (double)(pos.getY() + 0.5), bz = (double)(pos.getZ() + 0.5);
        return Math.abs(x - bx) <= range && Math.abs(z - bz) <= range &&
            // check both feet position and eye position, and ensure it will work if player is higher than 2 blocks
            ((y <= by && by <= ey) || Math.min(Math.abs(y - by), Math.abs(ey - by)) <= range);
    }

    public static boolean isInRange(@Nullable BlockPos pos, @Nullable Level world, @Nullable Player player, int x, int y, int z, int maxRange) {
        if (pos == null || world == null || player == null)
            return false;

        // It shouldn't multiply by 2 here, but it should have the same behavior as isInRange when x == y == z == range
        x = Math.min(Math.abs(x), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        y = Math.min(Math.abs(y), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        z = Math.min(Math.abs(z), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        return isPlayerInBlockRangeXYZ(pos, world, player, (double) x, (double) y, (double) z, maxRange);
    }

    private static boolean isPlayerInBlockRangeXYZ(@NotNull BlockPos pos, @NotNull Level world, @NotNull Player player, double dx, double dy, double dz, int maxRange) {
        if (maxRange != -1 && player.getLevel() != world)
            return false;

        double x = player.getX(), y = player.getY(), ey = player.getEyeY(), z = player.getZ();
        if (ey > y) {
            double tmp = ey;
            ey = y;
            y = tmp;
        }
        double bx = (double)(pos.getX() + 0.5), by = (double)(pos.getY() + 0.5), bz = (double)(pos.getZ() + 0.5);
        return Math.abs(x - bx) <= dx && Math.abs(z - bz) <= dz &&
            ((y <= by && by <= ey) || Math.min(Math.abs(y - by), Math.abs(ey - by)) <= dy);
    }

    public static boolean isInRange(@Nullable BlockPos blockPos, @Nullable Player player, @Nullable Level world, @NotNull BlockPos firstPos, @NotNull BlockPos secondPos, int maxRange) {
        if (blockPos == null || world == null || player == null)
            return false;

        double i = Math.abs(player.getX() - blockPos.getX());
        double j = Math.abs(player.getZ() - blockPos.getZ());
        // Check if the distance of the player is within the max range of the player detector
        // Use manhattan distance, not euclidean distance to keep same behavior than other `isInRange` functions
        if (i + j > (maxRange != -1 ? maxRange : Integer.MAX_VALUE))
            return false;
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(firstPos, secondPos)).contains(player);
    }

    public static Direction getDirection(FrontAndTop orientation, String computerSide) throws LuaException {
        if (computerSide == null)
            throw new LuaException("null is not a valid side");

        computerSide = computerSide.toLowerCase(Locale.ROOT);
        Direction dir = Direction.byName(computerSide);
        if (dir != null)
            return dir;

        Direction top = orientation.top();
        Direction front = orientation.front();

        final ComputerSide side = ComputerSide.valueOfInsensitive(computerSide);
        if (side == null)
            throw new LuaException(computerSide + " is not a valid side");

        if (front.getAxis() == Direction.Axis.Y) {
            return switch (side) {
                case FRONT -> front;
                case BACK -> front.getOpposite();
                case TOP -> top;
                case BOTTOM -> top.getOpposite();
                case RIGHT -> top.getClockWise();
                case LEFT -> top.getCounterClockWise();
            };
        } else {
            return switch (side) {
                case FRONT -> front;
                case BACK -> front.getOpposite();
                case TOP -> Direction.UP;
                case BOTTOM -> Direction.DOWN;
                case RIGHT -> front.getCounterClockWise();
                case LEFT -> front.getClockWise();
            };
        }

    }

    public static ComputerSide getComputerSide(FrontAndTop orientation, Direction direction) {
        Direction top = orientation.top();
        Direction front = orientation.front();

        if (direction == front) {
            return ComputerSide.FRONT;
        }
        if (direction == front.getOpposite()) {
            return ComputerSide.BACK;
        }
        if (front.getAxis() == Direction.Axis.Y) {
            if (direction == top) {
                return ComputerSide.TOP;
            }
            if (direction == top.getOpposite()) {
                return ComputerSide.BOTTOM;
            }
            if (direction == top.getClockWise()) {
                return ComputerSide.RIGHT;
            }
            if (direction == top.getCounterClockWise()) {
                return ComputerSide.LEFT;
            }
        }
        if (direction == front.getClockWise()) {
            return ComputerSide.RIGHT;
        }
        if (direction == front.getCounterClockWise()) {
            return ComputerSide.LEFT;
        }
        if (direction == Direction.UP) {
            return ComputerSide.TOP;
        }
        return ComputerSide.BOTTOM;
    }
}
