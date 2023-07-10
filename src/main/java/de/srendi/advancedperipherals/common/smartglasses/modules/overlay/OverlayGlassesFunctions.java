package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;

public class OverlayGlassesFunctions implements IModuleFunctions {

    @LuaFunction
    public final String test() {
        return "Hello World! I'm an overlay module!";
    }


}
