package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BoxRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class BoxObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 4;

    private static final IThreeDObjectRenderer RENDERER = new BoxRenderer();

    public BoxObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public BoxObject(UUID player) {
        super(player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
    }

    public static BoxObject decode(FriendlyByteBuf buffer) {
        Optional<BoxObject> optionalObject = RenderableObject.baseDecode(buffer, BoxObject::new);
        if (optionalObject.isEmpty())
            return null;

        boolean disableDepthTest = buffer.readBoolean();
        boolean disableCulling = buffer.readBoolean();;

        BoxObject clientObject = optionalObject.get();
        clientObject.disableDepthTest = disableDepthTest;
        clientObject.disableCulling = disableCulling;

        return clientObject;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
