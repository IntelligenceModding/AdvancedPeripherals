package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.lib.codec.StreamCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ItemObject extends RenderableObject {
    public Holder<Item> item = null;

    public ItemObject(OverlayModule module) {
        super(module);
    }

    public ItemObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<ItemObject> getType() {
        return APOverlayObjects.ITEM.get();
    }

    @LuaFunction
    public final String getItem() {
        return this.item == null ? null : this.item.unwrapKey().get().location().toString();
    }

    @LuaFunction
    public final void setItem(Optional<String> item) {
        String item0 = item.orElse(null);
        if (item0 == null) {
            this.item = null;
        } else {
            ResourceLocation name = ResourceLocation.tryParse(item0);
            this.item = BuiltInRegistries.ITEM.getHolder(ResourceKey.create(Registries.ITEM, name)).orElse(null);
        }
        this.markAndTryUpdate("item");
    }

    @Override
    protected void registerFieldEncoders(BiConsumer<String, FieldEncoder<?>> registrar) {
        super.registerFieldEncoders(registrar);
        registrar.accept("item", new FieldEncoder<>(
            StreamCodec.of(
                (buf, item) -> buf.writeId(BuiltInRegistries.ITEM.asHolderIdMap(), item),
                (buf) -> buf.readById(BuiltInRegistries.ITEM.asHolderIdMap())
            ),
            () -> this.item,
            (item) -> this.item = item
        ));
    }

    @Override
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        super.setPropertiesFromTable(initFields);
        this.setItem(initFields.optString("item"));
    }

    @Override
    public String toString() {
        return "ItemObject{" +
                "item='" + item + '\'' +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
