package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.LineRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * Just a line
 */
public class LineObject extends RenderableObject {
    public static final int TYPE_ID = 8;

    private static final LineRenderer RENDERER = new LineRenderer();

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int pixelSize = 4;

    public LineObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
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
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public void setPixelated(boolean pixelated) {
        this.pixelated = pixelated;
        this.sendUpdate();
    }

    @LuaFunction
    public boolean isPixelated() {
        return pixelated;
    }

    @LuaFunction
    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
        this.sendUpdate();
    }

    @LuaFunction
    public int getPixelSize() {
        return pixelSize;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeBoolean(pixelated);
        buffer.writeInt(pixelSize);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.pixelated = buffer.readBoolean();
        this.pixelSize = buffer.readInt();
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
