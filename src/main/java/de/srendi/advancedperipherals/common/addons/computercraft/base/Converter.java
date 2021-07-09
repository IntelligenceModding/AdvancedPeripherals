package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.CallbackI;

import java.util.*;
import java.util.stream.Collectors;

public class Converter {

    public static Object posToObject(@NotNull BlockPos pos) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

    public static Object stackToObject(@NotNull ItemStack stack) {
        Map<String, Object> map = (Map<String, Object>) itemToObject(stack.getItem());
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName());
        map.put("maxStackSize", stack.getMaxStackSize());
        return map;
    }

    public static Object itemToObject(@NotNull Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("tags", tagsToList(item.getTags()));
        map.put("name", item.getRegistryName().toString());
        return map;
    }

    public static List<String> tagsToList(@NotNull Set<ResourceLocation> tags) {
        if(tags.isEmpty())
            return null;
        return tags.stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }

    public static Direction getDirection(Direction facing, ComputerSide computerSide) {
        Direction output = Direction.DOWN;
        if (computerSide == ComputerSide.FRONT) output = facing;
        if (computerSide == ComputerSide.BACK)
            output = facing.getOpposite();
        if (computerSide == ComputerSide.TOP) output = Direction.UP;
        if (computerSide == ComputerSide.BOTTOM) output = Direction.DOWN;
        if (computerSide == ComputerSide.RIGHT)
            output = facing.getCounterClockWise();
        if (computerSide == ComputerSide.LEFT)
            output = facing.getClockWise();
        return output;
    }

}
