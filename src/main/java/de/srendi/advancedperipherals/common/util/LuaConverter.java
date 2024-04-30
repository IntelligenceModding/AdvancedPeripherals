package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    public static Map<String, Object> entityToLua(Entity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("uuid", entity.getStringUUID());
        data.put("name", entity.getName().getString());
        data.put("tags", entity.getTags());
        data.put("canFreeze", entity.canFreeze());
        data.put("isGlowing", entity.isCurrentlyGlowing());
        data.put("isInWall", entity.isInWall());
        return data;
    }

    public static Map<String, Object> livingEntityToLua(LivingEntity entity) {
        Map<String, Object> data = entityToLua(entity);
        data.put("health", entity.getHealth());
        data.put("maxHealth", entity.getMaxHealth());
        data.put("lastDamageSource", entity.getLastDamageSource() == null ? null : entity.getLastDamageSource().toString());
        return data;
    }

    public static Map<String, Object> animalToLua(Animal animal, ItemStack itemInHand) {
        Map<String, Object> data = livingEntityToLua(animal);
        data.put("baby", animal.isBaby());
        data.put("inLove", animal.isInLove());
        data.put("aggressive", animal.isAggressive());
        if (animal instanceof IForgeShearable shareable && !itemInHand.isEmpty()) {
            data.put("shareable", shareable.isShearable(itemInHand, animal.level, animal.blockPosition()));
        }
        return data;
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand) {
        if (entity instanceof Animal animal) return animalToLua(animal, itemInHand);
        if (entity instanceof LivingEntity livingEntity) return livingEntityToLua(livingEntity);
        return entityToLua(entity);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand);
        data.put("x", entity.getX() - pos.getX());
        data.put("y", entity.getY() - pos.getY());
        data.put("z", entity.getZ() - pos.getZ());
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

    public static Object posToObject(BlockPos pos) {
        if (pos == null) return null;

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

    public static Map<String, Object> fluidStackToObject(@NotNull FluidStack stack) {
        if (stack.isEmpty()) return new HashMap<>();
        Map<String, Object> map = fluidToObject(stack.getFluid());
        CompoundTag nbt = stack.copy().getOrCreateTag();
        map.put("count", stack.getAmount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("fingerprint", FluidUtil.getFingerprint(stack));
        return map;
    }

    public static Map<String, Object> itemStackToObject(@NotNull ItemStack itemStack, int amount) {
        ItemStack stack = itemStack.copy();
        stack.setCount(amount);
        return itemStackToObject(stack);
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
