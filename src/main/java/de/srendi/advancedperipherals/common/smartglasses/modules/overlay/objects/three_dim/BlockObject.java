package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockObject extends BoxObject {
    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<BlockState>> BLOCKSTATE_STREAM_CODEC = StreamCodec.of(
        (buf, state) -> buf.writeOptional(state, (b, s) -> b.writeJsonWithCodec(BlockState.CODEC, s)),
        buf -> buf.readOptional((b) -> b.readJsonWithCodec(BlockState.CODEC))
    );

    private BlockState block = null;

    @BooleanProperty
    public boolean tintAll = false;

    public BlockObject(OverlayModule module) {
        super(module);
    }

    public BlockObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<?> getType() {
        return APOverlayObjects.BLOCK.get();
    }

    public BlockState getBlockState() {
        return this.block;
    }

    @LuaFunction
    public final MethodResult getBlock() {
        if (this.block == null) {
            return MethodResult.of();
        }
        return MethodResult.of(
            BuiltInRegistries.BLOCK.getKey(this.block.getBlock()).toString(),
            this.getStates()
        );
    }

    @LuaFunction
    public final void setBlock(Optional<String> block, Optional<Map<?, ?>> states) throws LuaException {
        String block0 = block.orElse(null);
        if (block0 == null) {
            this.block = null;
        } else {
            ResourceLocation name = ResourceLocation.tryParse(block0);
            Holder<Block> holder = BuiltInRegistries.BLOCK.getHolder(name).orElse(null);
            if (holder == null) {
                this.block = null;
            } else {
                BlockState state = holder.value().defaultBlockState();
                Map<?, ?> statesMap = states.orElse(Map.of());
                if (!statesMap.isEmpty()) {
                    state = this.updateStates(state, statesMap);
                }
                this.block = state;
            }
        }
        this.markAndTryUpdate("block");
    }

    @SuppressWarnings("rawtypes")
    @LuaFunction
    public final Map<String, Object> getStates() {
        return this.block == null ? null : this.block.getValues().entrySet().stream().collect(
            Collectors.toMap(
                e -> e.getKey().getName(),
                e -> {
                    Comparable<?> v = e.getValue();
                    if (v instanceof Boolean) {
                        return v;
                    }
                    if (v instanceof Number n && !(v instanceof Long)) {
                        return Double.valueOf(n.doubleValue());
                    }
                    return ((Property) e.getKey()).getName(v);
                }
            )
        );
    }

    @LuaFunction
    public final void setStates(Map<?, ?> states) throws LuaException {
        if (this.block == null) {
            return;
        }
        if (states.isEmpty()) {
            return;
        }
        BlockState state = this.updateStates(this.block.getBlock().defaultBlockState(), states);
        if (this.block != state) {
            this.block = state;
        }
        this.markAndTryUpdate("block");
    }

    protected BlockState updateStates(BlockState state, Map<?, ?> states) throws LuaException {
        Map<String, Property<?>> propMap = state.getProperties().stream().collect(Collectors.toMap(Property::getName, Function.identity()));
        for (Map.Entry<?, ?> entry : states.entrySet()) {
            @SuppressWarnings("rawtypes")
            Property property = propMap.get(entry.getKey());
            if (property == null) {
                throw new LuaException("Invalid property name \"" + entry.getKey() + "\"");
            }
            @SuppressWarnings("rawtypes")
            Optional<Comparable> value = Optional.empty();
            Object eValue = entry.getValue();
            if (eValue instanceof Boolean bool) {
                if (property.getPossibleValues().contains(bool)) {
                    value = Optional.of(bool);
                }
            } else if (eValue instanceof Number number) {
                double dValue = number.doubleValue();
                long lValue = number.longValue();
                value = property.getPossibleValues().stream()
                    .filter(v -> v instanceof Number n && n.doubleValue() == dValue && n.longValue() == lValue)
                    .findFirst();
            }
            if (value.isEmpty()) {
                value = property.getValue(eValue.toString());
            }
            if (value.isEmpty()) {
                throw new LuaException("Invalid property value `" + eValue + "` for " + entry.getKey());
            }
            state = state.setValue(property, value.get());
        }
        return state;
    }

    @Override
    protected void registerFieldEncoders(BiConsumer<String, FieldEncoder<?, ?>> registrar) {
        super.registerFieldEncoders(registrar);
        registrar.accept("block", new FieldEncoder<>(
            BLOCKSTATE_STREAM_CODEC,
            () -> Optional.ofNullable(this.block),
            (block) -> this.block = block.orElse(null)
        ));
    }

    @Override
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        super.setPropertiesFromTable(initFields);
        this.setBlock(initFields.optString("block"), initFields.optTable("states"));
    }
}
