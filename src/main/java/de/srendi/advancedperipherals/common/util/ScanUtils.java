package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ScanUtils {
    public static void relativeTraverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer) {
        traverseBlocks(world, center, radius, consumer, true);
    }

    public static void traverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer) {
        traverseBlocks(world, center, radius, consumer, false);
    }

    public static void traverseBlocks(Level world, Vec3 center, double radius, BiConsumer<BlockState, Vec3> consumer, boolean relativePosition) {
        final double x = center.x, y = center.y, z = center.z;
        final int minX = Mth.floor(x - radius), maxX = Mth.floor(x + radius);
        final int minY = Mth.floor(y - radius), maxY = Mth.floor(y + radius);
        final int minZ = Mth.floor(z - radius), maxZ = Mth.floor(z + radius);
        final BlockPos.MutableBlockPos subPos = new BlockPos.MutableBlockPos();
        for (int oX = minX; oX <= maxX; oX++) {
            for (int oY = minY; oY <= maxY; oY++) {
                for (int oZ = minZ; oZ <= maxZ; oZ++) {
                    BlockState blockState = world.getBlockState(subPos.set(oX, oY, oZ));
                    if (blockState.isAir()) {
                        continue;
                    }
                    consumer.accept(
                        blockState,
                        relativePosition
                            ? new Vec3(oX + 0.5 - x, oY + 0.5 - y, oZ + 0.5 - z)
                            : new Vec3(oX, oY, oZ)
                    );
                }
            }
        }
    }
}
