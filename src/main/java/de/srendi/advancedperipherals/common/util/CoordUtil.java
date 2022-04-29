package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CoordUtil {

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int range) {
        range = Math.min(range, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int x, int y, int z) {
        x = Math.min(x * 2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        y = Math.min(y * 2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        z = Math.min(z * 2, APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get());
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(BlockPos blockPos, PlayerEntity player, World world, BlockPos firstPos, BlockPos secondPos) {
        double i = player.getX() - blockPos.getX();
        double j = player.getZ() - blockPos.getZ();
        // Check if the distance of the player is within the max range of the player detector
        if(MathHelper.sqrt(i * i + j * j) > APConfig.PERIPHERALS_CONFIG.PLAYER_DET_MAX_RANGE.get())
            return false;


        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(firstPos, secondPos)).contains(player);
    }

}
