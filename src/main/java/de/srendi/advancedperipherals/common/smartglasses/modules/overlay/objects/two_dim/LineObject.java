package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.LineRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

/**
 * Just a line
 */
public class LineObject extends RenderableObject {
    public static final int TYPE_ID = 8;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int pixelSize = 4;

    private final IObjectRenderer renderer = new LineRenderer();

    public LineObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    /**
     * constructor for the client side initialization
     *
     * @param player the target player
     */
    public LineObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public void setPixelated(boolean pixelated) {
        this.pixelated = pixelated;
        getModule().update(this);
    }

    @LuaFunction
    public boolean isPixelated() {
        return pixelated;
    }

    @LuaFunction
    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
        getModule().update(this);
    }

    @LuaFunction
    public int getPixelSize() {
        return pixelSize;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeBoolean(pixelated);
        buffer.writeInt(pixelSize);
    }

    public static LineObject decode(FriendlyByteBuf buffer) {
        Optional<LineObject> optionalObject = RenderableObject.baseDecode(buffer, LineObject::new);
        if (optionalObject.isEmpty())
            return null;

        boolean pixelated = buffer.readBoolean();
        int pixelSize = buffer.readInt();

        LineObject clientObject = optionalObject.get();

        clientObject.pixelated = pixelated;
        clientObject.pixelSize = pixelSize;

        return clientObject;

    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }
}
