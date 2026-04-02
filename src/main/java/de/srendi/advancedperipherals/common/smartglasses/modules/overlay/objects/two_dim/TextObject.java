package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TextObject extends RenderableObject {
    public static final int TYPE_ID = 2;

    private static final TextRenderer RENDERER = new TextRenderer();

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
    }

    public TextObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public final String getContent() {
        return content;
    }

    @LuaFunction
    public final void setContent(String content) {
        this.content = content;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getFontSize() {
        return fontSize;
    }

    @LuaFunction
    public final void setFontSize(double fontSize) {
        this.fontSize = (float) fontSize;
        this.sendUpdate();
    }

    @LuaFunction
    public final boolean isShadow() {
        return shadow;
    }

    @LuaFunction
    public final void setShadow(boolean shadow) {
        this.shadow = shadow;
        this.sendUpdate();
    }

    @LuaFunction
    public final boolean isCenter() {
        return center;
    }

    @LuaFunction
    public final void setCenter(boolean center) {
        this.center = center;
        this.sendUpdate();
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeUtf(content);
        buffer.writeFloat(fontSize);
        buffer.writeBoolean(shadow);
        buffer.writeBoolean(center);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.content = buffer.readUtf();
        this.fontSize = buffer.readFloat();
        this.shadow = buffer.readBoolean();
        this.center = buffer.readBoolean();
    }

}
