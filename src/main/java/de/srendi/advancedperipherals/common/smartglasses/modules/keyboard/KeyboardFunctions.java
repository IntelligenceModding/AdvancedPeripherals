package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;

public class KeyboardFunctions implements IModuleFunctions {

    private final KeyboardModule keyboardModule;

    public KeyboardFunctions(KeyboardModule keyboardModule) {
        this.keyboardModule = keyboardModule;
    }

    @LuaFunction
    public final boolean isCapturingMouse() {
        return keyboardModule.isCapturingMouse();
    }

    @LuaFunction
    public final void setCaptureMouse(boolean enable) {
        keyboardModule.setCaptureMouse(enable);
    }
}
