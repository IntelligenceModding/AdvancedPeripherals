package de.srendi.advancedperipherals.common.setup;

import com.mojang.serialization.Codec;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral.DetectionType;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnusedMethod")
public class APDataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<String, Long>>> ABILITY_COOLDOWNS = registerStringKeyMap("cooldowns", Codec.LONG, ByteBufCodecs.VAR_LONG);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BINDING_COMPUTER = registerInt("binding_computer");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> CHUNKY_ID = registerUUID("chunky_id");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> CONSUMED_ENTITY_COMPOUND = registerNBT("consumed_entity_compound");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<NonNegativeId>> DISK_ID = registerNonNegativeId("disk_id");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> ENTITY_TRANSFER = registerNBT("entity_transfer");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUEL_CONSUMPTION_RATE = registerInt("fuel_consumption_rate");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackStorage>> ITEMS = registerItemStackStorage("items");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> KEYBOARD_OPENED = registerBoolean("keyboard_opened");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> MODULE_DATAS = registerDataComponent("module_datas");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = registerUUID("owner_id");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<String, GlobalPos>>> POINT_DATA_MARK = registerStringKeyMap("point_data_mark", GlobalPos.CODEC, GlobalPos.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ROTATION_CHARGE_SETTING = registerInt("rotation_charge_setting");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> STORED_DATA = registerDataComponent("stored_data");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WORLD_DATA_MARK = registerString("world_data_mark");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CALCULATE_PERIODICALLY = registerBoolean("calculate_periodically");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> CURRENT_DISTANCE = registerFloat("current_distance");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DetectionType>> DETECTION_TYPE = registerEnum("detection_type", DetectionType.values());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> HANDLING_INTERACTION_BUTTONS = registerByte("handling_interaction_buttons");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IGNORE_TRANSPARENT = registerBoolean("ignore_transparent");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> MAX_RANGE = registerFloat("max_range");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHOW_LASER = registerBoolean("show_laser");

    public static void register() {
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> simple(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return APRegistration.DATA_COMPONENT_TYPES.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> registerBoolean(String name) {
        return simple(name, builder -> builder.persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<String>> registerString(String name) {
        return simple(name, builder -> builder.persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<DataComponentPatch>> registerDataComponent(String name) {
        return simple(name, builder -> builder.persistent(DataComponentPatch.CODEC)
                .networkSynchronized(DataComponentPatch.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> registerByte(String name) {
        return simple(name, builder -> builder.persistent(Codec.BYTE)
                .networkSynchronized(ByteBufCodecs.BYTE));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerInt(String name) {
        return simple(name, builder -> builder.persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Long>> registerLong(String name) {
        return simple(name, builder -> builder.persistent(Codec.LONG)
                .networkSynchronized(ByteBufCodecs.VAR_LONG));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> registerFloat(String name) {
        return simple(name, builder -> builder.persistent(Codec.FLOAT)
                .networkSynchronized(ByteBufCodecs.FLOAT));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<NonNegativeId>> registerNonNegativeId(String name) {
        return simple(name, builder -> builder.persistent(NonNegativeId.CODEC)
                .networkSynchronized(NonNegativeId.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> registerUUID(String name) {
        return simple(name, builder -> builder.persistent(UUIDUtil.CODEC)
                .networkSynchronized(UUIDUtil.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> registerNBT(String name) {
        return simple(name, builder -> builder.persistent(CustomData.CODEC)
                .networkSynchronized(CustomData.STREAM_CODEC));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Component>> registerComponent(String name) {
        return simple(name, builder -> builder.persistent(ComponentSerialization.FLAT_CODEC)
                .networkSynchronized(ComponentSerialization.STREAM_CODEC)
                .cacheEncoding());
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<T>>> registerResourceKey(String name, ResourceKey<? extends Registry<T>> registryKey) {
        return simple(name, builder -> builder.persistent(ResourceKey.codec(registryKey))
                .networkSynchronized(ResourceKey.streamCodec(registryKey)));
    }

    private static <T extends Enum<T> & StringRepresentable> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerEnum(String name, T... values) {
        return simple(name, builder -> builder.persistent(StringRepresentable.fromEnum(() -> values))
                .networkSynchronized(NeoForgeStreamCodecs.enumCodec(values[0].getDeclaringClass())));
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackStorage>> registerItemStackStorage(String name) {
        return simple(name, builder -> builder.persistent(ItemStackStorage.CODEC)
                .networkSynchronized(ItemStackStorage.STREAM_CODEC));
    }

    private static <K, V> DeferredHolder<DataComponentType<?>, DataComponentType<Map<K, V>>> registerMap(
        String name,
        Codec<K> keyCodec,
        Codec<V> valueCodec,
        StreamCodec<? super ByteBuf, K> keyStreamCodec,
        StreamCodec<? super ByteBuf, V> valueStreamCodec
    ) {
        return simple(
            name,
            builder -> builder
                .persistent(Codec.unboundedMap(keyCodec, valueCodec))
                .networkSynchronized(
                    ByteBufCodecs.map(
                        // type cast here is necessary cause weird generics
                        (IntFunction<Map<K, V>>) HashMap::new,
                        keyStreamCodec,
                        valueStreamCodec
                    )
                        .map(Collections::unmodifiableMap, Function.identity())
                )
        );
    }

    private static <V> DeferredHolder<DataComponentType<?>, DataComponentType<Map<String, V>>> registerStringKeyMap(
        String name,
        Codec<V> valueCodec,
        StreamCodec<? super ByteBuf, V> valueStreamCodec
    ) {
        return registerMap(name, Codec.STRING, valueCodec, ByteBufCodecs.STRING_UTF8, valueStreamCodec);
    }
}
