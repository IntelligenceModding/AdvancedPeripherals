package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LuaConverter {

    public static Map<String, Object> entityToLua(Entity entity, boolean detailed) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("uuid", entity.getStringUUID());
        EntityType<?> type = entity.getType();
        data.put("type", type.getDescriptionId());
        data.put("category", type.getCategory());
        data.put("canBurn", entity.fireImmune());
        data.put("canFreeze", entity.canFreeze());
        if (detailed) {
            Team team = entity.getTeam();
            data.put("teamName", team == null ? null : team.getName());
            data.put("inFluid", entity.isInFluidType());
            data.put("dimension", entity.getLevel().dimension().location().toString());
            data.put("isGlowing", entity.isCurrentlyGlowing());
            data.put("isUnderWater", entity.isUnderWater());
            data.put("isInLava", entity.isInLava());
            data.put("isInWall", entity.isInWall());
        }
        return data;
    }

    public static Map<String, Object> livingEntityToLua(LivingEntity entity, boolean detailed) {
        Map<String, Object> data = entityToLua(entity, detailed);
        data.put("baby", entity.isBaby());
        data.put("health", entity.getHealth());
        data.put("maxHealth", entity.getMaxHealth());
        data.put("scale", entity.getScale());
        data.put("experienceReward", entity.getExperienceReward());
        data.put("armorValue", entity.getArmorValue());
        data.put("mobType", mobTypeToString(entity.getMobType()));
        data.put("sensitiveToWater", entity.isSensitiveToWater());
        if (detailed) {
            data.put("noActionTime", entity.getNoActionTime());
            data.put("lastHurtMob", entityToUUIDString(entity.getLastHurtMob()));
            data.put("lastHurtByMob", entityToUUIDString(entity.getLastHurtByMob()));
            data.put("killCredit", entityToUUIDString(entity.getKillCredit()));
            DamageSource lastDamageSource = entity.getLastDamageSource();
            data.put("lastDamageSource", lastDamageSource == null ? null : LuaConverter.damageSourceToObject(lastDamageSource));
            data.put("arrowCount", entity.getArrowCount());
            data.put("stingerCount", entity.getStingerCount());
            data.put("yHeadRot", entity.getYHeadRot());
            data.put("fallFlyingTicks", entity.getFallFlyingTicks());
            data.put("sleeping", entity.isSleeping());
            if (entity.isUsingItem()) {
                data.put("blocking", entity.isBlocking());
                Map<String, Object> useItemData = new HashMap<>();
                useItemData.put("useItem", itemStackToObject(entity.getUseItem()));
                useItemData.put("usedItemHand", entity.getUsedItemHand().name());
                useItemData.put("useItemRemainingTicks", entity.getUseItemRemainingTicks());
                useItemData.put("ticksUsingItem", entity.getTicksUsingItem());
                data.put("useItemData", useItemData);
            }
            Map<String, Object> effMap = new HashMap<>();
            entity.getActiveEffectsMap().forEach((key, value) -> {
                effMap.put(key.getDescriptionId(), effectToObject(value));
            });
            data.put("effects", effMap);
        }
        return data;
    }

    public static Map<String, Object> playerToLua(Player player, boolean detailed) {
        Map<String, Object> data = livingEntityToLua(player, detailed);
        data.put("secondaryUseActive", player.isSecondaryUseActive());
        data.put("xpNeededForNextLevel", player.getXpNeededForNextLevel());
        data.put("creative", player.isCreative());
        data.put("score", player.getScore());
        data.put("luck", player.getLuck());
        Inventory inv = player.getInventory();
        data.put("handSlot", inv.selected);
        if (detailed) {
            data.put("hasContainerOpen", player.hasContainerOpen());
            Map<Integer, Object> invMap = new HashMap<>();
            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                ItemStack item = inv.getItem(slot);
                if (!item.isEmpty()) {
                    invMap.put(slot, itemStackToObject(item));
                }
            }
            data.put("inventory", invMap);
        }
        return data;
    }

    public static Map<String, Object> mobToLua(Mob mob, boolean detailed) {
        Map<String, Object> data = livingEntityToLua(mob, detailed);
        data.put("noAI", mob.isNoAi());
        data.put("leftHanded", mob.isLeftHanded());
        data.put("aggressive", mob.isAggressive());
        data.put("target", entityToUUIDString(mob.getTarget()));
        data.put("leashHolder", entityToUUIDString(mob.getLeashHolder()));
        MobSpawnType mobSpawnType = mob.getSpawnType();
        data.put("spawnType", mobSpawnType == null ? null : mobSpawnType.name());
        return data;
    }

    public static Map<String, Object> ageableMobToLua(AgeableMob ageableMob, boolean detailed) {
        Map<String, Object> data = mobToLua(ageableMob, detailed);
        data.put("canBreed", ageableMob.canBreed());
        data.put("baby", ageableMob.isBaby());
        return data;
    }

    public static Map<String, Object> animalToLua(Animal animal, @Nullable ItemStack itemInHand, boolean detailed) {
        Map<String, Object> data = ageableMobToLua(animal, detailed);
        data.put("canFallInLove", animal.canFallInLove());
        data.put("inLove", animal.isInLove());
        if (itemInHand != null) {
            data.put("shearable", animal instanceof IForgeShearable shearable
                    && !itemInHand.isEmpty()
                    && shearable.isShearable(itemInHand, animal.level, animal.blockPosition()));
        }
        return data;
    }

    public static Map<String, Object> completeEntityToLua(Entity entity) {
        return completeEntityToLua(entity, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, boolean detailed) {
        return completeEntityToLua(entity, null, detailed);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand) {
        return completeEntityToLua(entity, itemInHand, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand, boolean detailed) {
        if (entity instanceof Animal animal) return animalToLua(animal, itemInHand, detailed);
        if (entity instanceof AgeableMob ageableMob) return ageableMobToLua(ageableMob, detailed);
        if (entity instanceof Mob mob) return mobToLua(mob, detailed);
        if (entity instanceof Player player) return playerToLua(player, detailed);
        if (entity instanceof LivingEntity livingEntity) return livingEntityToLua(livingEntity, detailed);
        return entityToLua(entity, detailed);
    }

    public static Map<String, Object> completeEntityWithRelativePositionToLua(Entity entity, BlockPos pos) {
        return completeEntityWithRelativePositionToLua(entity, pos, false);
    }

    public static Map<String, Object> completeEntityWithRelativePositionToLua(Entity entity, BlockPos pos, boolean detailed) {
        return completeEntityWithRelativePositionToLua(entity, null, pos, detailed);
    }

    public static Map<String, Object> completeEntityWithRelativePositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos, boolean detailed) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand, detailed);
        data.put("relativePos", relativePositionToLua(entity, pos));
        return data;
    }

    public static Map<String, Object> relativePositionToLua(Entity entity, BlockPos pos) {
        Map<String, Object> data = new HashMap<>();
        data.put("x", entity.getX() - pos.getX());
        data.put("y", entity.getY() - pos.getY());
        data.put("z", entity.getZ() - pos.getZ());
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

    @Nullable
    public static Map<String, Object> posToObject(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(3);
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

    @Nullable
    public static Map<String, Object> itemStackToObject(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = itemToObject(stack.getItem());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("maxStackSize", stack.getMaxStackSize());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("fingerprint", ItemUtil.getFingerprint(stack));
        return map;
    }

    public static Map<String, Object> itemStackToObject(@NotNull ItemStack itemStack, int amount) {
        ItemStack stack = itemStack.copy();
        stack.setCount(amount);
        return itemStackToObject(stack);
    }

    @Nullable
    public static Map<String, Object> fluidStackToObject(@NotNull FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = fluidToObject(stack.getFluid());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getAmount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("fingerprint", FluidUtil.getFingerprint(stack));
        return map;
    }

    /**
     * Returns the stack but with a slot entry. Used to prevent zero indexed tables
     *
     * @param stack the item stack
     * @param slot  the slot of the item
     * @return a Map containing proper item stack details
     * @see InventoryManagerPeripheral#getItems()
     */
    @Nullable
    public static Map<String, Object> stackToObjectWithSlot(@NotNull ItemStack stack, int slot) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> map = itemStackToObject(stack);
        map.put("slot", slot + 1);
        return map;
    }

    public static Map<String, Object> itemToObject(@NotNull Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("tags", tagsToList(() -> item.builtInRegistryHolder().tags()));
        map.put("name", ItemUtil.getRegistryKey(item).toString());
        return map;
    }

    public static Map<String, Object> fluidToObject(@NotNull Fluid fluid) {
        Map<String, Object> map = new HashMap<>();
        FluidType fluidType = fluid.getFluidType();
        map.put("tags", tagsToList(() -> fluid.builtInRegistryHolder().tags()));
        map.put("name", FluidUtil.getRegistryKey(fluid).toString());
        map.put("density", fluidType.getDensity());
        map.put("lightLevel", fluidType.getLightLevel());
        map.put("temperature", fluidType.getTemperature());
        map.put("viscosity", fluidType.getViscosity());
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
        map.put("entity", entityToUUIDString(damageSource.getEntity()));
        map.put("directEntity", entityToUUIDString(damageSource.getDirectEntity()));
        map.put("foodExhaustion", damageSource.getFoodExhaustion());
        Vec3 damageSourcePosition = damageSource.getSourcePosition();
        map.put("sourcePosition", damageSourcePosition == null ? null : LuaConverter.positionToObject(damageSourcePosition));
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

    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        if (tags.get().findAny().isEmpty()) return Collections.emptyList();
        return tags.get().map(LuaConverter::tagToString).toList();
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }

    // BlockPos tricks
    public static BlockPos convertToBlockPos(Map<?, ?> table) throws LuaException {
        if (!table.containsKey("x") || !table.containsKey("y") || !table.containsKey("z")) {
            throw new LuaException("Table should contains key 'x', 'y' and 'z'");
        }
        if (!(table.get("x") instanceof Number x) || !(table.get("y") instanceof Number y) || !(table.get("z") instanceof Number z)) {
            throw new LuaException("Position should be numbers");
        }
        return new BlockPos(x.intValue(), y.intValue(), z.intValue());
    }

    public static BlockPos convertToBlockPos(BlockPos center, Map<?, ?> table) throws LuaException {
        BlockPos relative = convertToBlockPos(table);
        return new BlockPos(center.getX() + relative.getX(), center.getY() + relative.getY(), center.getZ() + relative.getZ());
    }

    public static Object effectToObject(MobEffectInstance effect) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", effect.getDescriptionId());
        map.put("duration", effect.getDuration());
        map.put("amplifier", effect.getAmplifier());
        return map;
    }

    @Nullable
    private static String entityToUUIDString(@Nullable Entity entity) {
        return entity == null ? null : entity.getUUID().toString();
    }
}
