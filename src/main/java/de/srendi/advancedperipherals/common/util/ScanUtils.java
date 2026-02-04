package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ScanUtils {
    public static void relativeTraverseBlocks(Level world, BlockPos center, int radius, BiConsumer<BlockState, BlockPos> consumer) {
        traverseBlocks(world, center, radius, consumer, true);
    }

    public static void traverseBlocks(Level world, BlockPos center, int radius, BiConsumer<BlockState, BlockPos> consumer) {
        traverseBlocks(world, center, radius, consumer, false);
    }

    public static void traverseBlocks(Level world, BlockPos center, int radius, BiConsumer<BlockState, BlockPos> consumer, boolean relativePosition) {
        final int x = center.getX(), y = center.getY(), z = center.getZ();
        final int minX = x - radius, maxX = x + radius;
        final int minY = y - radius, maxY = y + radius;
        final int minZ = z - radius, maxZ = z + radius;
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
                            ? new BlockPos(oX - x, oY - y, oZ - z)
                            : new BlockPos(oX, oY, oZ)
                    );
                }
            }
        }
    }

    public static Map<String, String> serializeState(BlockState state){
        Map<String, String> map = new HashMap<>();

        state.getValues().forEach(((prop, val) -> {
            map.put(prop.getName(), val.toString());
        }));

        return map;
    }
}
