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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3dc;
import org.joml.Vector3d;

import java.util.Locale;
import java.util.Map;

public class CoordUtil {

    /**
     * isInRange checks if the player is in the range
     *
     * @param pos the position to start check
     * @param world the world to start check
     * @param player the player going to be check
     * @param range the range that user want to reach, must be -1, 0, or a positive number
     * @param maxRange the maximum range the user can reach, must be -1, 0, or a positive number
     *
     * @return If the player is in the {@code range} as well as in the {@code maxRange}, or {@code range} and {@code maxRange} are -1
     */
    public static boolean isInRange(@Nullable Vec3 pos, @Nullable Level world, @Nullable Player player, int range, int maxRange) {
        // There are rare cases where these can be null. For example if a player detector pocket computer runs while not in a player inventory
        // Fixes https://github.com/SirEndii/AdvancedPeripherals/issues/356
        if (pos == null || world == null || player == null) {
            return false;
        }

        if (range == 0 || maxRange == 0) {
            return false;
        }
        if (range < 0) {
            if (maxRange < 0) {
                return true;
            }
            range = maxRange;
        } else if (maxRange > 0 && range > maxRange) {
            range = maxRange;
        }
        return isPlayerInBlockRange(pos, world, player, (double) range);
    }

    // To fix issue #439
    private static boolean isPlayerInBlockRange(@NotNull Vec3 pos, @NotNull Level world, @NotNull Player player, double range) {
        if (range != -1 && player.level() != world)
            return false;

        double x = player.getX(), y = player.getY(), ey = player.getEyeY(), z = player.getZ();
        if (ey > y) { // Ensure following code will work when eye position is lower than feet position
            double tmp = ey;
            ey = y;
            y = tmp;
        }
        return Math.abs(x - pos.x) <= range && Math.abs(z - pos.z) <= range &&
            // check both feet position and eye position, and ensure it will work if player is higher than 2 blocks
            ((y <= pos.y && pos.y <= ey) || Math.min(Math.abs(y - pos.y), Math.abs(ey - pos.y)) <= range);
    }

    public static boolean isInRange(@Nullable Vec3 pos, @Nullable Level world, @Nullable Player player, int x, int y, int z, int maxRange) {
        if (pos == null || world == null || player == null)
            return false;

        // It shouldn't multiply by 2 here, but it should have the same behavior as isInRange when x == y == z == range
        x = Math.min(Math.abs(x), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        y = Math.min(Math.abs(y), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        z = Math.min(Math.abs(z), maxRange != -1 ? maxRange : Integer.MAX_VALUE);
        return isPlayerInBlockRangeXYZ(pos, world, player, (double) x, (double) y, (double) z, maxRange);
    }

    private static boolean isPlayerInBlockRangeXYZ(@NotNull Vec3 pos, @NotNull Level world, @NotNull Player player, double dx, double dy, double dz, int maxRange) {
        if (maxRange != -1 && player.level() != world)
            return false;

        double x = player.getX(), y = player.getY(), ey = player.getEyeY(), z = player.getZ();
        if (ey > y) {
            double tmp = ey;
            ey = y;
            y = tmp;
        }
        return Math.abs(x - pos.x) <= dx && Math.abs(z - pos.z) <= dz &&
            ((y <= pos.y && pos.y <= ey) || Math.min(Math.abs(y - pos.y), Math.abs(ey - pos.y)) <= dy);
    }

    public static boolean isInRange(@Nullable Vec3 pos, @Nullable Player player, @Nullable Level world, @NotNull BlockPos firstPos, @NotNull BlockPos secondPos, int maxRange) {
        if (pos == null || world == null || player == null)
            return false;

        double x = Math.abs(player.getX() - pos.x);
        double y = Math.abs(player.getY() - pos.y);
        double z = Math.abs(player.getZ() - pos.z);
        // Check if the distance of the player is within the max range of the player detector
        // Use manhattan distance, not euclidean distance to keep same behavior than other `isInRange` functions
        if (maxRange != -1 && x + y + z > maxRange)
            return false;
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, AABB.encapsulatingFullBlocks(firstPos, secondPos)).contains(player);
    }

    @Nullable
    public static Direction getDirection(FrontAndTop orientation, String computerSide) throws LuaException {
        if (computerSide == null) {
            return null;
        }

        computerSide = computerSide.toLowerCase(Locale.ROOT);
        Direction dir = Direction.byName(computerSide);
        if (dir != null)
            return dir;

        Direction top = orientation.top();
        Direction front = orientation.front();

        final ComputerSide side = ComputerSide.valueOfInsensitive(computerSide);
        if (side == null) {
            return null;
        }

        if (front.getAxis() == Direction.Axis.Y) {
            return switch (side) {
                case FRONT -> front;
                case BACK -> front.getOpposite();
                case TOP -> top;
                case BOTTOM -> top.getOpposite();
                case RIGHT -> top.getClockWise();
                case LEFT -> top.getCounterClockWise();
            };
        }
        return switch (side) {
            case FRONT -> front;
            case BACK -> front.getOpposite();
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case RIGHT -> front.getCounterClockWise();
            case LEFT -> front.getClockWise();
        };
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

    /**
     * put "x", "y", "z", and/or "f", "r", "u" coords based on configuration
     * @see putFRUCoords
     */
    public static void putRelativeCoords(Map<? super String, ? super Double> data, double x, double y, double z, Matrix3dc orientation) {
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        putFRUCoords(data, x, y, z, orientation);
    }

    /**
     * put "f", "r", "u" coords into the map
     */
    public static void putFRUCoords(Map<? super String, ? super Double> data, double x, double y, double z, Matrix3dc orientation) {
        Vector3d rpos = orientation.transformTranspose(new Vector3d(x, y, z));
        data.put("r", rpos.x);
        data.put("u", rpos.y);
        data.put("f", rpos.z);
    }
}
