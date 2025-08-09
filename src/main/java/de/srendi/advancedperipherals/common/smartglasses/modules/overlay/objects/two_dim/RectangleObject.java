package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.RectangleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import org.w3c.dom.css.Rect;

import java.util.Optional;
import java.util.UUID;

/**
 * Just a rectangle
 */
public class RectangleObject extends RenderableObject {
    public static final int TYPE_ID = 0;

    private final IObjectRenderer renderer = new RectangleRenderer();

    public RectangleObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    /**
     * constructor for the client side initialization
     *
     * @param player the target player
     */
    public RectangleObject(UUID player) {
        super(player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
    }

    public static RectangleObject decode(FriendlyByteBuf buffer) {
        Optional<RectangleObject> optionalObject = RenderableObject.baseDecode(buffer, RectangleObject::new);
        return optionalObject.orElse(null);
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }
}
