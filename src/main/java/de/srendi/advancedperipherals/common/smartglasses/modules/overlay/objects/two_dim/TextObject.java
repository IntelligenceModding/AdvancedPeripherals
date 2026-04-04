package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TextObject extends RenderableObject {
    public static final int TYPE_ID = 2;

    private static final TextRenderer RENDERER = new TextRenderer();

    @StringProperty
    public String content = "";

    @FloatingNumberProperty(min = 0, max = 128)
    public float fontSize = 1;

    @BooleanProperty(getterPrefix = "has")
    public boolean shadow = false;

    @BooleanProperty
    public boolean center = false;

    public TextObject(OverlayModule module) {
        super(module);
    }

    public TextObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "text";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
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
