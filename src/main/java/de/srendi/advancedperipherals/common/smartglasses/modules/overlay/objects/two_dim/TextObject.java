package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TextObject extends RenderableObject {
    public static final int TYPE_ID = 2;

    private final IObjectRenderer renderer = new TextRenderer();

    @StringProperty
    public String content = "";

    @FloatingNumberProperty(min = 0, max = 128)
    public float fontSize = 1;

    @BooleanProperty
    public boolean shadow = false;

    public TextObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public TextObject(UUID player) {
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

    @LuaFunction
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @LuaFunction
    public boolean isShadow() {
        return shadow;
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
        buffer.writeBoolean(shadow);
    }

    public static TextObject decode(FriendlyByteBuf buffer) {
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
        int z = buffer.readInt();
        int sizeX = buffer.readInt();
        int sizeY = buffer.readInt();
        String content = buffer.readUtf();
        float fontSize = buffer.readFloat();
        boolean shadow = buffer.readBoolean();

        TextObject clientObject = new TextObject(player);
        clientObject.setId(objectId);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.z = z;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;
        clientObject.content = content;
        clientObject.fontSize = fontSize;
        clientObject.shadow = shadow;

        return clientObject;
    }

}
