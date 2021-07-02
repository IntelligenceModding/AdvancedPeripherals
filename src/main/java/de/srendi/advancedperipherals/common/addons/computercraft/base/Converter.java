package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.system.CallbackI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converter {

    public static Object posToObject(BlockPos pos) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

    public static Object stackToObject(ItemStack stack) {
        Map<String, Object> map = (Map<String, Object>) itemToObject(stack.getItem());
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName());
        map.put("maxStackSize", stack.getMaxStackSize());
        return map;
    }

    public static Object itemToObject(Item item) {
        Map<String, Object> map = new HashMap<>();
        List<String> tags = item.getTags().stream().map(ResourceLocation::toString).collect(Collectors.toList());
        map.put("tags", tags);
        map.put("name", item.getRegistryName().toString());
        return map;
    }

}
