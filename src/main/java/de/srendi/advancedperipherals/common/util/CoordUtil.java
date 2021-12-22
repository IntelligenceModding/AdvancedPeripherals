package de.srendi.advancedperipherals.common.util;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoordUtil {

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int range) {
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    public static boolean isInRange(BlockPos pos, World world, PlayerEntity player, int x, int y, int z) {
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    public static boolean isInRange(PlayerEntity player, World world, BlockPos firstPos, BlockPos secondPos) {
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(firstPos, secondPos)).contains(player);
    }

}
