package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoordUtil {

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int range) {
        range = Math.min(range, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int x, int y, int z) {
        x = Math.min(x*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        y = Math.min(y*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        z = Math.min(z*2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(PlayerEntity player, World world, BlockPos firstPos, BlockPos secondPos) {
        int xOffset = Math.min(Math.abs(firstPos.getX())+Math.abs(secondPos.getX()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        int yOffset = Math.min(Math.abs(firstPos.getY())+Math.abs(secondPos.getY()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        int zOffset = Math.min(Math.abs(firstPos.getZ())+Math.abs(secondPos.getZ()), APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());

        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(firstPos.offset(xOffset, yOffset, zOffset), secondPos)).contains(player);
    }

}
