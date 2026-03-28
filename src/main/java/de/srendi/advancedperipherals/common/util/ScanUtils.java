package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ScanUtils {
    public static void traverseBlocks(Level world, Vec3 center, int radius, BiConsumer<BlockState, BlockPos> consumer) {
        final int x = Mth.floor(center.x), y = Mth.floor(center.y), z = Mth.floor(center.z);
        final int minX = x - radius, maxX = x + radius;
        final int minY = y - radius, maxY = y + radius;
        final int minZ = z - radius, maxZ = z + radius;
        for (BlockPos subPos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockState state = world.getBlockState(subPos);
            if (!state.isAir()) {
                consumer.accept(state, subPos);
            }
        }
    }
}
