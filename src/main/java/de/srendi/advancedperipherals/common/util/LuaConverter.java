package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LuaConverter {

    public static Map<String, Object> entityToLua(Entity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("teamName", ObjectUtil.nullableValue(entity.getTeam(), Team::getName));
        data.put("inFluid", entity.isInFluidType());
        data.put("dimension", entity.getLevel().dimension().location().toString());
        return data;
    }

    public static Map<String, Object> livingEntityToLua(LivingEntity livingEntity) {
        Map<String, Object> data = entityToLua(livingEntity);
        data.put("scale", livingEntity.getScale());
        data.put("experienceReward", livingEntity.getExperienceReward());
        data.put("noActionTime", livingEntity.getNoActionTime());
        data.put("lastHurtMob", ObjectUtil.nullableValue(livingEntity.getLastHurtMob(), Entity::getUUID));
        data.put("armorValue", livingEntity.getArmorValue());
        data.put("lastDamageSource", ObjectUtil.nullableValue(livingEntity.getLastDamageSource(), LuaConverter::damageSourceToObject));
        data.put("killCredit", ObjectUtil.nullableValue(livingEntity.getKillCredit(), Entity::getUUID));
        data.put("arrowCount", livingEntity.getArrowCount());
        data.put("stingerCount", livingEntity.getStingerCount());
        data.put("mobType", mobTypeToString(livingEntity.getMobType()));
        data.put("yHeadRot", livingEntity.getYHeadRot());
        data.put("fallFlyingTicks", livingEntity.getFallFlyingTicks());
        data.put("baby", livingEntity.isBaby());
        data.put("sensitiveToWater", livingEntity.isSensitiveToWater());
        data.put("sleeping", livingEntity.isSleeping());
        if (livingEntity.isUsingItem()) {
            data.put("blocking", livingEntity.isBlocking());
            Map<String, Object> useItemData = new HashMap<>();
            useItemData.put("useItem", itemStackToObject(livingEntity.getUseItem()));
            useItemData.put("usedItemHand", livingEntity.getUsedItemHand().name());
            useItemData.put("useItemRemainingTicks", livingEntity.getUseItemRemainingTicks());
            useItemData.put("ticksUsingItem", livingEntity.getTicksUsingItem());
            data.put("useItemData", useItemData);
        }
        return data;
    }

    public static Map<String, Object> playerToLua(Player player) {
        Map<String, Object> data = livingEntityToLua(player);
        data.put("secondaryUseActive", player.isSecondaryUseActive());
        data.put("hasContainerOpen", player.hasContainerOpen());
        data.put("xpNeededForNextLevel", player.getXpNeededForNextLevel());
        data.put("creative", player.isCreative());
        return data;
    }

    public static Map<String, Object> mobToLua(Mob mob) {
        Map<String, Object> data = livingEntityToLua(mob);
        data.put("noAI", mob.isNoAi());
        data.put("leftHanded", mob.isLeftHanded());
        data.put("aggressive", mob.isAggressive());
        data.put("target", ObjectUtil.nullableValue(mob.getTarget(), Entity::getUUID));
        data.put("leashHolder", ObjectUtil.nullableValue(mob.getLeashHolder(), Entity::getUUID));
        data.put("spawnType", ObjectUtil.nullableValue(mob.getSpawnType(), Enum::name));
        return data;
    }

    public static Map<String, Object> ageableMobToLua(AgeableMob ageableMob) {
        Map<String, Object> data = mobToLua(ageableMob);
        data.put("canBreed", ageableMob.canBreed());
        data.put("baby", ageableMob.isBaby());
        return data;
    }

    public static Map<String, Object> animalToLua(Animal animal) {
        Map<String, Object> data = ageableMobToLua(animal);
        data.put("canFallInLove", animal.canFallInLove());
        data.put("inLove", animal.isInLove());
        return data;
    }

    public static Map<String, Object> completeEntityToLua(Entity entity) {
        if (entity instanceof Animal animal) return animalToLua(animal);
        if (entity instanceof AgeableMob ageableMob) return ageableMobToLua(ageableMob);
        if (entity instanceof Mob mob) return mobToLua(mob);
        if (entity instanceof Player player) return playerToLua(player);
        if (entity instanceof LivingEntity livingEntity) return livingEntityToLua(livingEntity);
        return entityToLua(entity);
    }

    public static Map<String, Object> relativePositionToLua(Entity entity, BlockPos pos) {
        Map<String, Object> data = new HashMap<>();
        data.put("relativeX", entity.getX() - pos.getX());
        data.put("relativeY", entity.getY() - pos.getY());
        data.put("relativeZ", entity.getZ() - pos.getZ());
        return data;
    }

    public static Map<String, Object> vec3ToLua(Vec3 vec3) {
        Map<String, Object> data = new HashMap<>();
        data.put("x", vec3.x);
        data.put("y", vec3.y);
        data.put("z", vec3.z);
        return data;
    }

    /**
     * Block states to a lua representable object
     *
     * @param blockStateValue block state see {@link net.minecraft.world.level.block.state.BlockState#getValue(Property)}
     * @return the state cast to a lua representable object
     */
    public static Object stateToObject(Comparable<?> blockStateValue) {
        if (blockStateValue == null) {
            return null;
        } else if (blockStateValue instanceof Boolean || blockStateValue instanceof Number || blockStateValue instanceof String) {
            // Just return the value since lua can represent them just fine
            return blockStateValue;
        } else if (blockStateValue instanceof StringRepresentable stringRepresentable) {
            return stringRepresentable.getSerializedName();
        } else {
            return null;
        }
    }

    public static Map<String, Object> posToObject(BlockPos pos) {
        if (pos == null) return null;

        Map<String, Object> map = new HashMap<>(3);
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

    public static Map<String, Object> stackToObject(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return new HashMap<>();
        Map<String, Object> map = itemToObject(stack.getItem());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("maxStackSize", stack.getMaxStackSize());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("fingerprint", ItemUtil.getFingerprint(stack));
        return map;
    }

    public static Map<String, Object> stackToObject(@NotNull ItemStack itemStack, int amount) {
        ItemStack stack = itemStack.copy();
        stack.setCount(amount);
        return stackToObject(stack);
    }

    /**
     * Returns the stack but with a slot entry. Used to prevent zero indexed tables
     *
     * @param stack the item stack
     * @param slot  the slot of the item
     * @return a Map containing proper item stack details
     * @see InventoryManagerPeripheral#getItems()
     */
    public static Map<String, Object> stackToObjectWithSlot(@NotNull ItemStack stack, int slot) {
        if (stack.isEmpty()) return new HashMap<>();
        Map<String, Object> map = stackToObject(stack);
        map.put("slot", slot);
        return map;
    }

    public static Map<String, Object> itemToObject(@NotNull Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("tags", tagsToList(() -> item.builtInRegistryHolder().tags()));
        map.put("name", ItemUtil.getRegistryKey(item).toString());
        return map;
    }

    public static Map<String, Object> positionToObject(@NotNull Position position) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", position.x());
        map.put("y", position.y());
        map.put("z", position.z());
        return map;
    }

    public static Map<String, Object> damageSourceToObject(@NotNull DamageSource damageSource) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", damageSource.getMsgId());
        map.put("entity", ObjectUtil.nullableValue(damageSource.getEntity(), Entity::getUUID));
        map.put("directEntity", ObjectUtil.nullableValue(damageSource.getDirectEntity(), Entity::getUUID));
        map.put("foodExhaustion", damageSource.getFoodExhaustion());
        map.put("sourcePosition", ObjectUtil.nullableValue(damageSource.getSourcePosition(), LuaConverter::positionToObject));
        return map;
    }

    public static String mobTypeToString(@NotNull MobType mobType) {
        if (mobType == MobType.UNDEFINED) {
            return "undefined";
        } else if (mobType == MobType.UNDEAD) {
            return "undead";
        } else if (mobType == MobType.ARTHROPOD) {
            return "arthropod";
        } else if (mobType == MobType.ILLAGER) {
            return "illager";
        } else if (mobType == MobType.WATER) {
            return "water";
        }
        throw new IllegalArgumentException("Unrecognized MobType");
    }

    public static Map<String, Object> aabbToObject(@NotNull AABB aabb) {
        Map<String, Object> map = new HashMap<>();
        map.put("minX", aabb.minX);
        map.put("minY", aabb.minY);
        map.put("minZ", aabb.minZ);
        map.put("maxX", aabb.maxX);
        map.put("maxY", aabb.maxY);
        map.put("maxZ", aabb.maxZ);
        return map;
    }

    public static Map<Integer, Object> uuidToLua(UUID uuid) {
        int[] ints = UUIDUtil.uuidToIntArray(uuid);
        Map<Integer, Object> data = new HashMap<>();
        for (int i = 0; i < ints.length; i++) {
            data.put(i + 1, ints[i]);
        }
        return data;
    }

    public static UUID luaToUUID(Map<?, ?> uuidList) {
        int msb1 = (int) (double) uuidList.get(1.);
        int msb2 = (int) (double) uuidList.get(2.);
        int lsb1 = (int) (double) uuidList.get(3.);
        int lsb2 = (int) (double) uuidList.get(4.);
        long msb = (((long) msb1) << 32) | (msb2 & 0xffffffffL);
        long lsb = (((long) lsb1) << 32) | (lsb2 & 0xffffffffL);
        return new UUID(msb, lsb);
    }

    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        if (tags.get().findAny().isEmpty()) return Collections.emptyList();
        return tags.get().map(LuaConverter::tagToString).toList();
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }

    // BlockPos tricks
    public static BlockPos convertToBlockPos(Map<?, ?> table) throws LuaException {
        if (!table.containsKey("x") || !table.containsKey("y") || !table.containsKey("z"))
            throw new LuaException("Table should be block position table");
        if (!(table.get("x") instanceof Number x) || !(table.get("y") instanceof Number y) || !(table.get("z") instanceof Number z))
            throw new LuaException("Table should be block position table");
        return new BlockPos(x.intValue(), y.intValue(), z.intValue());
    }

    public static BlockPos convertToBlockPos(BlockPos center, Map<?, ?> table) throws LuaException {
        BlockPos relative = convertToBlockPos(table);
        return new BlockPos(center.getX() + relative.getX(), center.getY() + relative.getY(), center.getZ() + relative.getZ());
    }
}
