package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LuaConverter {

    public static Map<String, Object> entityToLua(Entity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("uuid", entity.getStringUUID());
        data.put("name", entity.getName().getString());
        data.put("tags", entity.getTags());
        return data;
    }

    public static Map<String, Object> animalToLua(AnimalEntity animal, ItemStack itemInHand) {
        Map<String, Object> data = entityToLua(animal);
        data.put("baby", animal.isBaby());
        data.put("inLove", animal.isInLove());
        data.put("aggressive", animal.isAggressive());
        if (animal instanceof IForgeShearable && !itemInHand.isEmpty()) {
            IForgeShearable shareable = (IForgeShearable) animal;
            data.put("shareable", shareable.isShearable(itemInHand, animal.level, animal.blockPosition()));
        }
        return data;
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand) {
        if (entity instanceof AnimalEntity)
            return animalToLua((AnimalEntity) entity, itemInHand);
        return entityToLua(entity);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand);
        data.put("x", entity.getX() - pos.getX());
        data.put("y", entity.getY() - pos.getY());
        data.put("z", entity.getZ() - pos.getZ());
        return data;
    }

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
        if (tags.isEmpty())
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
