package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LuaConverter {

    public static Map<String, Object> entityToLua(Entity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("uuid", entity.getStringUUID());
        data.put("name", entity.getName().getString());
        data.put("tags", entity.getTags());
        return data;
    }

    public static Map<String, Object> animalToLua(Animal animal, ItemStack itemInHand) {
        Map<String, Object> data = entityToLua(animal);
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
        return entityToLua(entity);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand);
        data.put("x", entity.getX() - pos.getX());
        data.put("y", entity.getY() - pos.getY());
        data.put("z", entity.getZ() - pos.getZ());
        return data;
    }

    public static Object posToObject(BlockPos pos) {
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
        CompoundTag nbt = stack.getTag();
        map.put("count", stack.getCount());
        map.put("displayName", stack.getDisplayName().getString());
        map.put("maxStackSize", stack.getMaxStackSize());
        if (nbt == null) {
            nbt = new CompoundTag();//ensure compatibility with lua programs relying on a non-nil value
        }
        map.put("nbt", NBTUtil.toLua(nbt));
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

    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        if (tags.get().findAny().isEmpty()) return Collections.emptyList();
        return tags.get().map(LuaConverter::tagToString).toList();
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }

    public static Direction getDirection(Direction facing, String computerSide) throws LuaException {
        if (Direction.byName(computerSide) != null) return Direction.byName(computerSide);
        if (Objects.equals(computerSide, ComputerSide.FRONT.toString())) return facing;
        if (Objects.equals(computerSide, ComputerSide.BACK.toString())) return facing.getOpposite();
        if (Objects.equals(computerSide, ComputerSide.TOP.toString())) return Direction.UP;
        if (Objects.equals(computerSide, ComputerSide.BOTTOM.toString())) return Direction.DOWN;
        if (Objects.equals(computerSide, ComputerSide.RIGHT.toString())) return facing.getCounterClockWise();
        if (Objects.equals(computerSide, ComputerSide.LEFT.toString())) return facing.getClockWise();

        throw new LuaException(computerSide + " is not a valid side");
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
