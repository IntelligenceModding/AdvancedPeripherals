package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class Text extends RenderableObject {
    public static final int ID = 2;

    private final IObjectRenderer renderer = new TextRenderer();

    @StringProperty
    public String content;

    public Text(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public Text(String id, UUID player) {
        super(id, player);
    }

    @LuaFunction
    public final String getContent() {
        return content;
    }

    @LuaFunction
    public final void setContent(String content) {
        this.content = content;
        getModule().update(this);
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(ID);
        super.encode(buffer);
        buffer.writeUtf(content);
    }

    public static Text decode(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        boolean hasValidUUID = buffer.readBoolean();
        if (!hasValidUUID) {
            AdvancedPeripherals.exception("Tried to decode a buffer for an OverlayObject but without a valid player as target.", new IllegalArgumentException());
            return null;
        }
        UUID player = buffer.readUUID();
        int color = buffer.readInt();
        float opacity = buffer.readFloat();

        int x = buffer.readInt();
        int y = buffer.readInt();
        int sizeX = buffer.readInt();
        int sizeY = buffer.readInt();
        String content = buffer.readUtf();

        Text clientObject = new Text(id, player);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;
        clientObject.content = content;

        return clientObject;
    }

}
