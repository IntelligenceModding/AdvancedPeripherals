package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LuaConverter {

    private static final CompoundTag EMPTY_TAG = new CompoundTag();
    private static final Map<Class<? extends Entity>, List<EntityConverter<?>>> ENTITY_CONVERTERS = new HashMap<>();

    /**
     * registerEntityConverter register a converter for a type of entity.
     * If an old converter exists, it will invoke the old one first before invoke the new converter.
     *
     * @param clazz     The entity's class
     * @param converter The {@link EntityConverter}
     */
    public static <T extends Entity> void registerEntityConverter(Class<T> clazz, EntityConverter<T> converter) {
        ENTITY_CONVERTERS.computeIfAbsent(clazz, (k) -> new ArrayList<>(1)).add(converter);
    }

    // register default entity converters
    static {
        registerEntityConverter(Entity.class, (entity, data, ctx) -> {
            data.put("id", entity.getId());
            data.put("uuid", entity.getStringUUID());
            if (entity.hasCustomName())
                data.put("customName", entity.getCustomName().getString());
            EntityType<?> type = entity.getType();
            data.put("displayName", type.getDescription().getString());
            data.put("name", type.builtInRegistryHolder().key().location().toString());
            if (ctx.detailed()) {
                data.put("type", type.getDescriptionId());
                data.put("category", type.getCategory().getName());
                data.put("canBurn", entity.fireImmune());
                data.put("canFreeze", entity.canFreeze());
                data.put("tags", entity.getTags());
                data.put("isGlowing", entity.isCurrentlyGlowing());
                data.put("isUnderWater", entity.isUnderWater());
                data.put("isInLava", entity.isInLava());
                data.put("isInWall", entity.isInWall());
                data.put("team", teamToLua(entity.getTeam()));
            }
        });
        registerEntityConverter(LivingEntity.class, (entity, data, ctx) -> {
            data.put("baby", entity.isBaby());
            data.put("health", entity.getHealth());
            data.put("maxHealth", entity.getMaxHealth());
            if (ctx.detailed()) {
                data.put("lastDamageSource", entity.getLastDamageSource() == null ? null : entity.getLastDamageSource().toString());
                Map<String, Object> effMap = new HashMap<>();
                entity.getActiveEffectsMap().forEach((key, value) -> {
                    effMap.put(key.getDescriptionId(), effectToLua(value));
                });
                data.put("effects", effMap);
            }
        });
        registerEntityConverter(Mob.class, (entity, data, ctx) -> {
            data.put("aggressive", entity.isAggressive());
        });
        registerEntityConverter(Animal.class, (entity, data, ctx) -> {
            data.put("inLove", entity.isInLove());
            if (ctx.detailed() && !ctx.itemInHand().isEmpty() && entity instanceof IForgeShearable shareable) {
                data.put("shareable", shareable.isShearable(ctx.itemInHand(), entity.level, entity.blockPosition()));
            }
        });
        registerEntityConverter(Player.class, (entity, data, ctx) -> {
            data.put("score", entity.getScore());
            data.put("luck", entity.getLuck());
            Inventory inv = entity.getInventory();
            data.put("handSlot", inv.selected);
            if (ctx.detailed()) {
                Map<Integer, Object> invMap = new HashMap<>();
                for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                    ItemStack item = inv.getItem(slot);
                    if (!item.isEmpty()) {
                        invMap.put(slot, itemStackToObject(item));
                    }
                }
                data.put("inventory", invMap);
            }
        });
    }

    @FunctionalInterface
    public interface EntityConverter<T extends Entity> {
        void entityToMap(T entity, Map<String, Object> data, Context ctx);

        record Context(boolean detailed, ItemStack itemInHand) {}
    }

    public static Map<String, Object> completeEntityToLua(Entity entity) {
        return completeEntityToLua(entity, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, boolean detailed) {
        return completeEntityToLua(entity, ItemStack.EMPTY, detailed);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand) {
        return completeEntityToLua(entity, itemInHand, false);
    }

    public static Map<String, Object> completeEntityToLua(Entity entity, ItemStack itemInHand, boolean detailed) {
        if (entity == null) {
            return null;
        }
        EntityConverter.Context ctx = new EntityConverter.Context(detailed, itemInHand);
        Map<String, Object> data = new HashMap<>();
        for (Class<?> entityClass = entity.getClass(); Entity.class.isAssignableFrom(entityClass); entityClass = entityClass.getSuperclass()) {
            List<EntityConverter<? extends Entity>> converters = ENTITY_CONVERTERS.get((Class<? extends Entity>) entityClass);
            if (converters != null) {
                for (EntityConverter<? extends Entity> converter : converters) {
                    ((EntityConverter<Entity>) converter).entityToMap(entity, data, ctx);
                }
            }
        }
        return data;
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, BlockPos pos) {
        return completeEntityWithPositionToLua(entity, pos, false);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, Vec3 pos) {
        return completeEntityWithPositionToLua(entity, pos, false);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, BlockPos pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos, detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, Vec3 pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos, detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, BlockPos pos, boolean detailed) {
        return completeEntityWithPositionToLua(entity, itemInHand, Vec3.atCenterOf(pos), detailed);
    }

    public static Map<String, Object> completeEntityWithPositionToLua(Entity entity, ItemStack itemInHand, Vec3 pos, boolean detailed) {
        Map<String, Object> data = completeEntityToLua(entity, itemInHand, detailed);
        data.put("x", entity.getX() - pos.x);
        data.put("y", entity.getY() - pos.y);
        data.put("z", entity.getZ() - pos.z);
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
    public static Object posToObject(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        Map<String, Object> properties = new HashMap<>(3);
        properties.put("x", pos.getX());
        properties.put("y", pos.getY());
        properties.put("z", pos.getZ());
        return properties;
    }

    @Nullable
    public static Map<String, Object> itemStackToObject(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemToObject(stack.getItem());
        DataComponentPatch components = stack.getComponentsPatch();
        if (nbt == null) {
            nbt = EMPTY_TAG;
        }
        properties.put("count", stack.getCount());
        properties.put("displayName", stack.getDisplayName().getString());
        properties.put("maxStackSize", stack.getMaxStackSize());
        try {
            properties.put("components", NBTUtil.toLua(DataComponentUtil.toNbt(components)));
        } catch (IllegalStateException ex) {
            AdvancedPeripherals.debug("Couldn't create components for Item Stack " + stack, ex);
        }
        properties.put("fingerprint", ItemUtil.getFingerprint(stack));
        return properties;
    }

    @Nullable
    public static Map<String, Object> itemStackToObject(@NotNull ItemStack stack, Level level) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemToObject(stack.getItem());
        DataComponentPatch components = stack.getComponentsPatch();
        properties.put("count", stack.getCount());
        properties.put("displayName", stack.getDisplayName().getString());
        properties.put("maxStackSize", stack.getMaxStackSize());
        try {
            properties.put("components", NBTUtil.toLua(DataComponentUtil.toNbt(components, level)));
        } catch (IllegalStateException ex) {
            AdvancedPeripherals.debug("Couldn't create components for Item Stack " + stack, ex);
        }
        properties.put("fingerprint", ItemUtil.getFingerprint(stack));
        return properties;
    }

    public static Map<String, Object> fluidStackToObject(@NotNull FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = fluidToObject(stack.getFluid());
        DataComponentPatch components = stack.getComponentsPatch();
        properties.put("count", stack.getAmount());
        properties.put("displayName", stack.getHoverName().getString());
        properties.put("fluidType", fluidTypeToObject(stack.getFluidType()));
        properties.put("components", NBTUtil.toLua(DataComponentUtil.toNbt(components)));
        properties.put("fingerprint", FluidUtil.getFingerprint(stack));
        return properties;
    }

    public static Map<String, Object> chemicalStackToObject(@NotNull ChemicalStack stack) {
        // In theory should not be called if the addon is not installed, but just to be save
        if (!APAddon.MEKANISM.isLoaded()) {
            return null;
        }

        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = chemicalToObject(stack.getChemical());
        properties.put("count", stack.getAmount());
        properties.put("displayName", stack.getTextComponent().getString());
        properties.put("fingerprint", ChemicalUtil.getFingerprint(stack));
        return properties;
    }

    public static Map<String, Object> fluidTypeToObject(FluidType type) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("viscosity", type.getViscosity());
        properties.put("density", type.getDensity());
        properties.put("canHydrate", type.canHydrate((Entity) null));
        properties.put("canExtinguish", type.canExtinguish(null));
        properties.put("canDrownIn", type.canDrownIn(null));
        properties.put("canSwim", type.canSwim(null));
        properties.put("canPushEntity", type.canPushEntity(null));
        properties.put("supportsBoating", type.supportsBoating(null));
        properties.put("canConvertToSource", type.canConvertToSource(null));
        properties.put("temperature", type.getTemperature(null));
        return properties;
    }

    public static Map<String, Object> itemStackToObject(@NotNull ItemStack itemStack, long count) {
        if (itemStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemStackToObject(itemStack);
        properties.put("count", count);
        return properties;
    }

    public static Map<String, Object> fluidStackToObject(@NotNull FluidStack fluidStack, long count) {
        if (fluidStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = fluidStackToObject(fluidStack);
        properties.put("count", count);
        return properties;
    }

    public static Map<String, Object> chemicalStackToObject(@NotNull ChemicalStack chemicalStack, long count) {
        // In theory should not be called if the addon is not installed, but just to be save
        if (!APAddon.MEKANISM.isLoaded()) {
            return null;
        }

        if (chemicalStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = chemicalStackToObject(chemicalStack);
        properties.put("count", count);
        return properties;
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
        Map<String, Object> properties = itemStackToObject(stack);
        properties.put("slot", slot + 1);
        return properties;
    }

    public static Map<String, Object> itemToObject(@NotNull Item item) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", tagsToList(() -> item.builtInRegistryHolder().tags()));
        properties.put("name", ItemUtil.getRegistryKey(item).toString());
        return properties;
    }

    public static Map<String, Object> fluidToObject(@NotNull Fluid fluid) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", tagsToList(() -> fluid.builtInRegistryHolder().tags()));
        properties.put("name", FluidUtil.getRegistryKey(fluid).toString());
        return properties;
    }

    public static Map<String, Object> chemicalToObject(@NotNull Chemical chemical) {
        // In theory should not be called if the addon is not installed, but just to be save
        if (!APAddon.MEKANISM.isLoaded()) {
            return null;
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", tagsToList(() -> MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical).tags()));
        properties.put("isGaseous", chemical.isGaseous());
        properties.put("radioactivity", chemical.isRadioactive());
        properties.put("name", ChemicalUtil.getRegistryKey(chemical).toString());
        return properties;
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
        // We do not use Collections.emptyList here to prevent an issue with textutils.serialise.
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
        // Use round here in case of 0.1 + 0.2 calculation
        return new BlockPos((int) (Math.round(x.doubleValue())), (int) (Math.round(y.doubleValue())), (int) (Math.round(z.doubleValue())));
    }

    public static BlockPos convertToBlockPos(BlockPos center, Map<?, ?> table) throws LuaException {
        BlockPos relative = convertToBlockPos(table);
        return new BlockPos(center.getX() + relative.getX(), center.getY() + relative.getY(), center.getZ() + relative.getZ());
    }

    public static Map<String, Object> effectToLua(MobEffectInstance effect) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", effect.getDescriptionId());
        map.put("duration", effect.getDuration());
        map.put("amplifier", effect.getAmplifier());
        return map;
    }

    public static Map<String, Object> teamToLua(Team team) {
        if (team == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", team.getName());
        map.put("color", team.getColor());
        return map;
    }

    public static Map<String, Object> shipToObject(ServerShip ship) {
        return shipToObject(ship, null);
    }

    public static Map<String, Object> shipToObject(ServerShip ship, Vec3 pos) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", ship.getId());
        map.put("slug", ship.getSlug());

        ShipTransform tf = ship.getTransform();

        Vector3dc shipPos = tf.getShipPositionInShipCoordinates();
        if (pos != null) {
            Vector3dc worldPos = tf.getShipPositionInWorldCoordinates();
            map.put("x", worldPos.x() - pos.x);
            map.put("y", worldPos.y() - pos.y);
            map.put("z", worldPos.z() - pos.z);
        }
        Quaterniondc rot = tf.getShipToWorldRotation();
        final double rotX = rot.x(), rotY = rot.y(), rotZ = rot.z(), rotW = rot.w();
        map.put("rotate", Map.of("x", rotX, "y", rotY, "z", rotZ, "w", rotW));

        AABBic box = ship.getShipAABB();
        if (box != null) {
            map.put("size", Map.of("x", box.maxX() - box.minX(), "y", box.maxY() - box.minY(), "z", box.maxZ() - box.minZ()));
            map.put("corner", Map.of("x", shipPos.x() - box.minX(), "y", shipPos.y() - box.minY(), "z", shipPos.z() - box.minZ()));
        }
        Vector3dc omega = ship.getOmega();
        map.put("omega", Map.of("x", omega.x(), "y", omega.y(), "z", omega.z()));
        Vector3dc velocity = ship.getVelocity();
        map.put("isStatic", ship.isStatic());
        map.put("velocity", Map.of("x", velocity.x(), "y", velocity.y(), "z", velocity.z()));

        ShipInertiaData data = ship.getInertiaData();
        map.put("mass", data.getMass());
        Vector3d com = tf.getShipToWorld().transformPosition(data.getCenterOfMassInShipSpace(), new Vector3d());
        if (pos != null) {
            map.put("centerOfMass", Map.of("x", com.x - pos.x, "y", com.y - pos.y, "z", com.z - pos.z));
        }
        return map;
    }

    public static Map<String, Object> shipToObjectOnShip(ServerShip ship, Vec3 pos) {
        Map<String, Object> map = shipToObject(ship);
        Vector3dc shipPos = ship.getTransform().getShipPositionInShipCoordinates();
        map.put("x", shipPos.x() - pos.x);
        map.put("y", shipPos.y() - pos.y);
        map.put("z", shipPos.z() - pos.z);
        Vector3dc com = ship.getInertiaData().getCenterOfMassInShipSpace();
        map.put("centerOfMass", Map.of("x", com.x() - pos.x, "y", com.y() - pos.y, "z", com.z() - pos.z));
        return map;
    }
}
