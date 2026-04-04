package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.LineRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Just a line
 */
public class LineObject extends RenderableObject {
    public static final int TYPE_ID = 8;

    private static final LineRenderer RENDERER = new LineRenderer();

    @FloatingNumberProperty
    public float endX = 0;

    @FloatingNumberProperty
    public float endY = 0;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int width = 4;

    public LineObject(OverlayModule module) {
        super(module);
    }

    /**
     * constructor for the client side initialization
     *
     * @param player the target player
     */
    public LineObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "line";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(this.endX);
        buffer.writeFloat(this.endY);
        buffer.writeBoolean(this.pixelated);
        buffer.writeInt(this.width);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.endX = buffer.readFloat();
        this.endY = buffer.readFloat();
        this.pixelated = buffer.readBoolean();
        this.width = buffer.readInt();
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
