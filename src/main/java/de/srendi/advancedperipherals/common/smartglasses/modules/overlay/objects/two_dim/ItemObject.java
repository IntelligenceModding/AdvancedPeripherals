package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ItemRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.UUID;

public class ItemObject extends RenderableObject {
    public static final int TYPE_ID = 3;

    private static final ItemRenderer RENDERER = new ItemRenderer();

    // @StringProperty
    public ResourceKey<Item> item = null;

    public ItemObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public ItemObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public final String getItem() {
        return this.item == null ? null : this.item.location().toString();
    }

    @LuaFunction
    public final void setItem(String item) {
        ResourceLocation name = ResourceLocation.tryParse(item);
        this.item = BuiltInRegistries.ITEM.containsKey(name) ? ResourceKey.create(Registries.ITEM, name) : null;
        this.sendUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        if (this.item == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeResourceKey(this.item);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.item = buffer.readBoolean() ? buffer.readResourceKey(Registries.ITEM) : null;
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }

    @Override
    public String toString() {
        return "ItemObject{" +
                "item='" + item + '\'' +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }
}
