package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class Text extends RenderableObject {
    public static final int TYPE_ID = 2;

    private final IObjectRenderer renderer = new TextRenderer();

    @StringProperty
    public String content = "";

    @FloatingNumberProperty(min = 0, max = 128)
    public float fontSize = 1;

    public Text(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public Text(UUID player) {
        super(player);
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

    @LuaFunction
    public double getFontSize() {
        return fontSize;
    }

    // For any reason, cc does not support float, only double. So we need to cast it here
    @LuaFunction
    public void setFontSize(double fontSize) {
        this.fontSize = (float) fontSize;
        getModule().update(this);
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(content);
        buffer.writeFloat(fontSize);
    }

    public static Text decode(FriendlyByteBuf buffer) {
        int objectId = buffer.readInt();
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
        float fontSize = buffer.readFloat();

        Text clientObject = new Text(player);
        clientObject.setId(objectId);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;
        clientObject.content = content;
        clientObject.fontSize = fontSize;

        return clientObject;
    }

}
