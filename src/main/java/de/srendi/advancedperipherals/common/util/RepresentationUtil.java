package de.srendi.advancedperipherals.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IForgeShearable;

import java.util.HashMap;
import java.util.Map;

public class RepresentationUtil {

    public static Map<String, Object> entityToLua(Entity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("entity_id", entity.getId());
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
}
