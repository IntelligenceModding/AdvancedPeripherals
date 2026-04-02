package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BoxRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;

import java.util.UUID;

public class BoxObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 4;

    private static final BoxRenderer RENDERER = new BoxRenderer();

    public BoxObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public BoxObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
