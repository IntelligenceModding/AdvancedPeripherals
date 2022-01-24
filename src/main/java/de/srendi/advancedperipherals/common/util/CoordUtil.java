package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CoordUtil {

    public static boolean isInRange(BlockPos pos, Level world, Player player, int range) {
        range = Math.min(range, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(),
                null, new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(BlockPos pos, Level world, Player player, int x, int y, int z) {
        x = Math.min(x*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        y = Math.min(y*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        z = Math.min(z*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(),
                null, new AABB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(Player player, Level world, BlockPos firstPos, BlockPos secondPos) {
        int xOffset = Math.min(Math.abs(firstPos.getX())+Math.abs(secondPos.getX()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        int yOffset = Math.min(Math.abs(firstPos.getY())+Math.abs(secondPos.getY()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        int zOffset = Math.min(Math.abs(firstPos.getZ())+Math.abs(secondPos.getZ()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());

        return world.getNearbyPlayers(TargetingConditions.forNonCombat(),
                null, new AABB(firstPos.offset(xOffset, yOffset, zOffset), secondPos)).contains(player);
    }

}
