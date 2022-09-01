package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CoordUtil {

    public static boolean isInRange(BlockPos pos, Level world, Player player, int range) {
        range = Math.min(range, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(BlockPos pos, Level world, Player player, int x, int y, int z) {
        x = Math.min(x * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        y = Math.min(y * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        z = Math.min(z * 2, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get());
        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(BlockPos blockPos, Player player, Level world, BlockPos firstPos, BlockPos secondPos) {
        double i = player.getX() - blockPos.getX();
        double j = player.getZ() - blockPos.getZ();
        // Check if the distance of the player is within the max range of the player detector
        if (Math.sqrt(i * i + j * j) > APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get()) return false;


        return world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, new AABB(firstPos, secondPos)).contains(player);
    }

}
