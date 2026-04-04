package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BlockRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class BlockObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 5;

    private static final BlockRenderer RENDERER = new BlockRenderer();

    // @StringProperty
    public ResourceKey<Block> block = null;

    private BlockState cachedBlockState = null;

    public BlockObject(OverlayModule module) {
        super(module);
    }

    public BlockObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "block";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public final String getBlock() {
        return this.block == null ? null : this.block.location().toString();
    }

    @LuaFunction
    public final void setBlock(Optional<String> block) {
        String block0 = block.orElse(null);
        if (block0 == null) {
            this.block = null;
        } else {
            ResourceLocation name = ResourceLocation.tryParse(block0);
            this.block = BuiltInRegistries.BLOCK.containsKey(name) ? ResourceKey.create(Registries.BLOCK, name) : null;
        }
        this.tryAutoUpdate();
    }

    public BlockState getBlockState() {
        if (this.cachedBlockState != null) {
            return this.cachedBlockState;
        }
        if (this.block == null) {
            return null;
        }
        Block block = BuiltInRegistries.BLOCK.get(this.block);
        this.cachedBlockState = block.defaultBlockState();
        return this.cachedBlockState;
    }

    @Override
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        super.setPropertiesFromTable(initFields);
        this.setBlock(initFields.optString("block"));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        if (this.block == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeResourceKey(this.block);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.block = buffer.readBoolean() ? buffer.readResourceKey(Registries.BLOCK) : null;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
