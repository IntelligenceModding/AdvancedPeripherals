package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ScanUtils {
    public static void traverseBlocks(Level world, Vec3 center, int radius, BiConsumer<BlockState, Vec3> consumer) {
        final int x = Mth.floor(center.x), y = Mth.floor(center.y), z = Mth.floor(center.z);
        final int minX = x - radius, maxX = x + radius;
        final int minY = y - radius, maxY = y + radius;
        final int minZ = z - radius, maxZ = z + radius;
        BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ).forEach((subPos) -> {
            BlockState blockState = world.getBlockState(subPos);
            if (blockState.isAir()) {
                return;
            }
            consumer.accept(blockState, new Vec3(subPos.getX() + 0.5 - x, subPos.getY() + 0.5 - y, subPos.getZ() + 0.5 - z));
        });
    }

    public static void traverseBlocks(Level world, BlockPos center, int radius, BiConsumer<BlockState, Vec3i> consumer) {
        final int x = center.getX(), y = center.getY(), z = center.getZ();
        final int minX = x - radius, maxX = x + radius;
        final int minY = y - radius, maxY = y + radius;
        final int minZ = z - radius, maxZ = z + radius;
        BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ).forEach((subPos) -> {
            BlockState blockState = world.getBlockState(subPos);
            if (blockState.isAir()) {
                return;
            }
            consumer.accept(blockState, subPos.subtract(center));
        });
    }
}
