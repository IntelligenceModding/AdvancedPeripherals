package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ItemRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class ItemObject extends RenderableObject {
    public static final int TYPE_ID = 3;

    private static final IObjectRenderer RENDERER = new ItemRenderer();

    @StringProperty
    public String item = "minecraft:air";

    public ItemObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public ItemObject(UUID player) {
        super(player);
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(item);
    }

    public static ItemObject decode(FriendlyByteBuf buffer) {
        Optional<ItemObject> optionalObject = RenderableObject.baseDecode(buffer, ItemObject::new);
        if (optionalObject.isEmpty())
            return null;

        String item = buffer.readUtf();

        ItemObject clientObject = optionalObject.get();
        clientObject.item = item;

        return clientObject;
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
