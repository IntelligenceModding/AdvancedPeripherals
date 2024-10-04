package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;

import java.util.UUID;

public abstract class ThreeDimensionalObject extends RenderableObject {

    public ThreeDimensionalObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public ThreeDimensionalObject(UUID player) {
        super(player);
    }
}
