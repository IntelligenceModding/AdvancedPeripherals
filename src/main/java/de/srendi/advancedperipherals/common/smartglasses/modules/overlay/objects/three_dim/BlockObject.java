package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class BlockObject extends ThreeDimensionalObject {
    public Holder<Block> block = null;

    private BlockState cachedBlockState = null;

    public BlockObject(OverlayModule module) {
        super(module);
    }

    public BlockObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<BlockObject> getType() {
        return APOverlayObjects.BLOCK.get();
    }

    @LuaFunction
    public final String getBlock() {
        return this.block == null ? null : this.block.unwrapKey().get().location().toString();
    }

    @LuaFunction
    public final void setBlock(Optional<String> block) {
        String block0 = block.orElse(null);
        if (block0 == null) {
            this.block = null;
        } else {
            ResourceLocation name = ResourceLocation.tryParse(block0);
            this.block = BuiltInRegistries.BLOCK.getHolder(name).orElse(null);
        }
        this.markAndTryUpdate("block");
    }

    public BlockState getBlockState() {
        if (this.cachedBlockState != null) {
            return this.cachedBlockState;
        }
        if (this.block == null) {
            return null;
        }
        this.cachedBlockState = this.block.value().defaultBlockState();
        // TODO: allow render specific block state
        return this.cachedBlockState;
    }

    @Override
    protected void registerFieldEncoders(BiConsumer<String, FieldEncoder<?, ?>> registrar) {
        registrar.accept("block", new FieldEncoder<>(
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.BLOCK)),
            () -> Optional.ofNullable(this.block),
            (block) -> this.block = block.orElse(null)
        ));
    }

    @Override
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        super.setPropertiesFromTable(initFields);
        this.setBlock(initFields.optString("block"));
    }
}
