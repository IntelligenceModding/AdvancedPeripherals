package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.CircleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CircleObject extends RenderableObject {
    public static final int TYPE_ID = 1;

    private static final CircleRenderer RENDERER = new CircleRenderer();

    @FixedPointNumberProperty(min = 0, max = Integer.MAX_VALUE)
    public int radius = 0;

    @BooleanProperty
    public boolean filled = true;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int borderWidth = 4;

    @FixedPointNumberProperty(min = 0, max = 100)
    public int segments = 25;

    public CircleObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
    }

    public CircleObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "circle";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(radius);
        buffer.writeBoolean(filled);
        buffer.writeBoolean(pixelated);
        buffer.writeInt(borderWidth);
        buffer.writeInt(segments);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.radius = buffer.readInt();
        this.filled = buffer.readBoolean();
        this.pixelated = buffer.readBoolean();
        this.borderWidth = buffer.readInt();
        this.segments = buffer.readInt();
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
