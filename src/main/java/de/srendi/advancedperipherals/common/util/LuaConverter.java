package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import mekanism.api.MekanismAPI;
import mekanism.api.MekanismAPITags;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LuaConverter {

    private static final Map<Class<? extends Entity>, List<EntityConverter<?>>> ENTITY_CONVERTERS = new HashMap<>();

    static {
        registerDefaultEntityConverters();
    }

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

    @FunctionalInterface
    public interface EntityConverter<T extends Entity> {
        void entityToMap(T entity, Map<String, Object> data, Context ctx);

        record Context(boolean detailed, ItemStack itemInHand, Vec3 position) {}
    }

    private static final EntityConverter.Context EMPTY_ENTITY_CONVERTER_CONTEXT = new EntityConverter.Context(false, null, null);

    public static EntityContextBuilder entityContextBuilder() {
        return new EntityContextBuilder();
    }

    public static final class EntityContextBuilder {
        private boolean detailed = false;
        private ItemStack itemInHand = null;
        private Vec3 position = null;

        public EntityContextBuilder detailed(boolean detailed) {
            this.detailed = detailed;
            return this;
        }

        public EntityContextBuilder detailed() {
            return this.detailed(true);
        }

        public EntityContextBuilder itemInHand(ItemStack stack) {
            this.itemInHand = stack;
            return this;
        }

        public EntityContextBuilder position(Vec3 pos) {
            this.position = pos;
            return this;
        }

        public EntityContextBuilder position(BlockPos pos) {
            return this.position(pos.getCenter());
        }

        public EntityConverter.Context build() {
            return new EntityConverter.Context(this.detailed, this.itemInHand, this.position);
        }
    }

    public static Map<String, Object> entityToLua(Entity entity) {
        return entityToLua(entity, EMPTY_ENTITY_CONVERTER_CONTEXT);
    }

    public static Map<String, Object> entityToLua(Entity entity, EntityConverter.Context ctx) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        for (Class<?> entityClass = entity.getClass(); Entity.class.isAssignableFrom(entityClass); entityClass = entityClass.getSuperclass()) {
            List<EntityConverter<?>> converters = ENTITY_CONVERTERS.get(entityClass);
            if (converters == null) {
                continue;
            }
            for (EntityConverter<?> converter : converters) {
                ((EntityConverter<Entity>) converter).entityToMap(entity, data, ctx);
            }
        }
        Vec3 pos = ctx.position();
        if (pos != null) {
            data.put("x", entity.getX() - pos.x);
            data.put("y", entity.getY() - pos.y);
            data.put("z", entity.getZ() - pos.z);
        }
        return data;
    }

    private static void registerDefaultEntityConverters() {
        registerEntityConverter(Entity.class, (entity, data, ctx) -> {
            data.put("id", entity.getId());
            data.put("uuid", entity.getStringUUID());
            if (entity.hasCustomName()) {
                data.put("customName", entity.getCustomName().getString());
            }
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
                DamageSource lastDamageSource = entity.getLastDamageSource();
                data.put("lastDamageSource", lastDamageSource == null ? null : lastDamageSource.toString());
                Map<String, Object> effMap = new HashMap<>();
                entity.getActiveEffectsMap().forEach((key, value) -> {
                    effMap.put(key.value().getDescriptionId(), effectToLua(value));
                });
                data.put("effects", effMap);
            }
        });
        registerEntityConverter(Mob.class, (entity, data, ctx) -> {
            data.put("aggressive", entity.isAggressive());
        });
        registerEntityConverter(Animal.class, (entity, data, ctx) -> {
            data.put("inLove", entity.isInLove());
            if (ctx.detailed() && !ctx.itemInHand().isEmpty() && entity instanceof IShearable shareable) {
                data.put("shareable", shareable.isShearable(null, ctx.itemInHand(), entity.level(), entity.blockPosition()));
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
                        invMap.put(slot, itemStackToLua(item));
                    }
                }
                data.put("inventory", invMap);
            }
        });
    }

    /**
     * Block states to a lua representable object
     *
     * @param stateValue block state see {@link net.minecraft.world.level.block.state.BlockState#getValue(Property)}
     * @return the state cast to a lua representable object
     */
    public static Object stateToLua(Comparable<?> stateValue) {
        if (stateValue == null) {
            return null;
        }
        if (stateValue instanceof Boolean || stateValue instanceof Number || stateValue instanceof String) {
            // Just return the value since lua can represent them just fine
            return stateValue;
        }
        if (stateValue instanceof StringRepresentable stringRepresentable) {
            return stringRepresentable.getSerializedName();
        }
        return null;
    }

    public static Map<String, Object> blockStateValuesToLua(BlockState state) {
        return Map.ofEntries(
            state.getValues()
                .entrySet()
                .stream()
                .map(((entry) -> Map.entry(entry.getKey().getName(), stateToLua(entry.getValue()))))
                .toArray(Map.Entry[]::new)
        );
    }

    private static final LRUCache<BlockState, Map<String, Object>> BLOCKSTATES_CACHE = new LRUCache<>(256);

    public static Map<String, Object> blockStateToLua(BlockState state0) {
        Map<String, Object> data = BLOCKSTATES_CACHE.computeIfAbsent(state0, (state) -> {
            Block block = state.getBlock();
            ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
            return Map.of(
                "name", name == null ? null : name.toString(),
                "tags", getHolderTags(block.builtInRegistryHolder()),
                "state", blockStateValuesToLua(state)
            );
        });

        return data;
    }

    @Nullable
    public static Object posToLua(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        Map<String, Object> properties = new HashMap<>(3);
        properties.put("x", pos.getX());
        properties.put("y", pos.getY());
        properties.put("z", pos.getZ());
        return properties;
    }

    public static Map<String, Object> itemToLua(@NotNull Item item) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", getHolderTags(item.builtInRegistryHolder()));
        properties.put("name", ItemUtil.getRegistryKey(item).toString());
        return properties;
    }

    public static Map<String, Object> fluidToLua(@NotNull Fluid fluid) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", getHolderTags(fluid.builtInRegistryHolder()));
        properties.put("name", FluidUtil.getRegistryKey(fluid).toString());
        return properties;
    }

    public static Map<String, Object> chemicalToLua(@NotNull Chemical chemical) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tags", getHolderTags(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical)));
        properties.put("name", ChemicalUtil.getRegistryKey(chemical).toString());
        properties.put("radioactivity", chemical.isRadioactive());
        return properties;
    }

    @Nullable
    public static Map<String, Object> itemStackToLua(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemToLua(stack.getItem());
        DataComponentPatch components = stack.getComponentsPatch();
        properties.put("count", stack.getCount());
        properties.put("displayName", stack.getDisplayName().getString());
        properties.put("maxStackSize", stack.getMaxStackSize());
        properties.put("components", DataComponentUtil.patchToLua(components));
        properties.put("fingerprint", ItemUtil.getFingerprint(stack));
        return properties;
    }

    public static Map<String, Object> fluidStackToLua(@NotNull FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = fluidToLua(stack.getFluid());
        DataComponentPatch components = stack.getComponentsPatch();
        properties.put("count", stack.getAmount());
        properties.put("displayName", stack.getHoverName().getString());
        properties.put("type", fluidTypeToLua(stack.getFluidType()));
        properties.put("components", DataComponentUtil.patchToLua(components));
        properties.put("fingerprint", FluidUtil.getFingerprint(stack));
        return properties;
    }

    public static Map<String, Object> chemicalStackToLua(@NotNull ChemicalStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = chemicalToLua(stack.getChemical());
        properties.put("count", stack.getAmount());
        properties.put("displayName", stack.getTextComponent().getString());
        properties.put("fingerprint", ChemicalUtil.getFingerprint(stack));
        properties.put("isGaseous", stack.is(MekanismAPITags.Chemicals.GASEOUS));
        return properties;
    }

    public static Map<String, Object> fluidTypeToLua(@NotNull FluidType type) {
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

    public static Map<String, Object> itemStackToLua(@NotNull ItemStack itemStack, long count) {
        if (itemStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemStackToLua(itemStack);
        properties.put("count", count);
        return properties;
    }

    public static Map<String, Object> fluidStackToLua(@NotNull FluidStack fluidStack, long count) {
        if (fluidStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = fluidStackToLua(fluidStack);
        properties.put("count", count);
        return properties;
    }

    public static Map<String, Object> chemicalStackToLua(@NotNull ChemicalStack chemicalStack, long count) {
        if (chemicalStack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = chemicalStackToLua(chemicalStack);
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
    public static Map<String, Object> itemStackToLuaWithSlot(@NotNull ItemStack stack, int slot) {
        if (stack.isEmpty()) {
            return null;
        }
        Map<String, Object> properties = itemStackToLua(stack);
        properties.put("slot", slot + 1);
        return properties;
    }

    private static final LRUCache<ResourceKey<?>, List<String>> HOLDER2TAGS_CACHE = new LRUCache<>(512);

    public static <T> List<String> getHolderTags(final Holder<T> holder) {
        final ResourceKey<T> holderKey = holder.unwrapKey().orElse(null);
        if (holderKey == null) {
            return tagsToList(holder.tags());
        }
        synchronized (HOLDER2TAGS_CACHE) {
            return HOLDER2TAGS_CACHE.computeIfAbsent(holderKey, (k) -> tagsToList(holder.tags()));
        }
    }

    public static <T> List<String> tagsToList(Stream<TagKey<T>> tags) {
        return tags.map(LuaConverter::tagToString).toList();
    }

    private static final LRUCache<TagKey<?>, String> TAG2STRING_CACHE = new LRUCache<>(1024);

    public static String tagToString(@NotNull TagKey<?> tag) {
        synchronized (TAG2STRING_CACHE) {
            return TAG2STRING_CACHE.computeIfAbsent(tag, (t) -> registryToSlashString(t.registry()) + t.location().toString());
        }
    }

    private static final LRUCache<ResourceKey<?>, String> REGISTRY2SLASH_STRING_CACHE = new LRUCache<>(256);

    private static String registryToSlashString(final ResourceKey<? extends Registry<?>> key) {
        synchronized (REGISTRY2SLASH_STRING_CACHE) {
            return REGISTRY2SLASH_STRING_CACHE.computeIfAbsent(key, (k) -> k.location().toString() + "/");
        }
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

    // public static Map<String, Object> shipToObject(ServerShip ship) {
    //     return shipToObject(ship, null);
    // }

    // public static Map<String, Object> shipToObject(ServerShip ship, Vec3 pos) {
    //     Map<String, Object> map = new HashMap<>();

    //     map.put("id", ship.getId());
    //     map.put("slug", ship.getSlug());

    //     ShipTransform tf = ship.getTransform();

    //     Vector3dc shipPos = tf.getShipPositionInShipCoordinates();
    //     if (pos != null) {
    //         Vector3dc worldPos = tf.getShipPositionInWorldCoordinates();
    //         map.put("x", worldPos.x() - pos.x);
    //         map.put("y", worldPos.y() - pos.y);
    //         map.put("z", worldPos.z() - pos.z);
    //     }
    //     Quaterniondc rot = tf.getShipToWorldRotation();
    //     final double rotX = rot.x(), rotY = rot.y(), rotZ = rot.z(), rotW = rot.w();
    //     map.put("rotate", Map.of("x", rotX, "y", rotY, "z", rotZ, "w", rotW));

    //     AABBic box = ship.getShipAABB();
    //     if (box != null) {
    //         map.put("size", Map.of("x", box.maxX() - box.minX(), "y", box.maxY() - box.minY(), "z", box.maxZ() - box.minZ()));
    //         map.put("corner", Map.of("x", shipPos.x() - box.minX(), "y", shipPos.y() - box.minY(), "z", shipPos.z() - box.minZ()));
    //     }
    //     Vector3dc omega = ship.getOmega();
    //     map.put("omega", Map.of("x", omega.x(), "y", omega.y(), "z", omega.z()));
    //     Vector3dc velocity = ship.getVelocity();
    //     map.put("isStatic", ship.isStatic());
    //     map.put("velocity", Map.of("x", velocity.x(), "y", velocity.y(), "z", velocity.z()));

    //     ShipInertiaData data = ship.getInertiaData();
    //     map.put("mass", data.getMass());
    //     Vector3d com = tf.getShipToWorld().transformPosition(data.getCenterOfMassInShipSpace(), new Vector3d());
    //     if (pos != null) {
    //         map.put("centerOfMass", Map.of("x", com.x - pos.x, "y", com.y - pos.y, "z", com.z - pos.z));
    //     }
    //     return map;
    // }

    // public static Map<String, Object> shipToObjectOnShip(ServerShip ship, Vec3 pos) {
    //     Map<String, Object> map = shipToObject(ship);
    //     Vector3dc shipPos = ship.getTransform().getShipPositionInShipCoordinates();
    //     map.put("x", shipPos.x() - pos.x);
    //     map.put("y", shipPos.y() - pos.y);
    //     map.put("z", shipPos.z() - pos.z);
    //     Vector3dc com = ship.getInertiaData().getCenterOfMassInShipSpace();
    //     map.put("centerOfMass", Map.of("x", com.x() - pos.x, "y", com.y() - pos.y, "z", com.z() - pos.z));
    //     return map;
    // }
}
