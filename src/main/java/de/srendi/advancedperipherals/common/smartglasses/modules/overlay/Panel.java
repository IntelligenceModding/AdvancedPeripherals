package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

/**
 * A panel is the standard panel which can contain multiple render-able objects in it.
 */
public class Panel extends OverlayObject {

    public Panel(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module);
        mapProperties(arguments);
    }

}
