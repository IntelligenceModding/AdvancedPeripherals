package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class TextObject extends RenderableObject {
    public static final int TYPE_ID = 2;

    private static final IObjectRenderer RENDERER = new TextRenderer();

    @StringProperty
    public String content = "";

    @FloatingNumberProperty(min = 0, max = 128)
    public float fontSize = 1;

    @BooleanProperty
    public boolean shadow = false;

    @BooleanProperty
    public boolean center = false;

    public TextObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public TextObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setContent(String content) {
        this.content = content;
        getModule().update(this);
    }

    @LuaFunction
    public final String getContent() {
        return content;
    }

    // For any reason, cc does not support float, only double. So we need to cast it here
    @LuaFunction
    public void setFontSize(double fontSize) {
        this.fontSize = (float) fontSize;
        getModule().update(this);
    }

    @LuaFunction
    public double getFontSize() {
        return fontSize;
    }

    @LuaFunction
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        getModule().update(this);
    }

    @LuaFunction
    public boolean isShadow() {
        return shadow;
    }

    @LuaFunction
    public void setCenter(boolean center) {
        this.center = center;
        getModule().update(this);
    }

    @LuaFunction
    public boolean isCenter() {
        return center;
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(content);
        buffer.writeFloat(fontSize);
        buffer.writeBoolean(shadow);
        buffer.writeBoolean(center);
    }

    public static TextObject decode(FriendlyByteBuf buffer) {
        Optional<TextObject> optionalObject = RenderableObject.baseDecode(buffer, TextObject::new);
        if (optionalObject.isEmpty())
            return null;

        String content = buffer.readUtf();
        float fontSize = buffer.readFloat();
        boolean shadow = buffer.readBoolean();
        boolean center = buffer.readBoolean();

        TextObject clientObject = optionalObject.get();
        clientObject.content = content;
        clientObject.fontSize = fontSize;
        clientObject.shadow = shadow;
        clientObject.center = center;

        return clientObject;
    }

}
