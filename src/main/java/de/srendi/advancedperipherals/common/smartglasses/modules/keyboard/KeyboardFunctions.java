package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;

public class KeyboardFunctions implements IModuleFunctions {

    private final KeyboardModule keyboardModule;
    private final SmartGlassesSideAccess access;

    public KeyboardFunctions(KeyboardModule keyboardModule, SmartGlassesSideAccess access) {
        this.keyboardModule = keyboardModule;
        this.access = access;
    }

    @LuaFunction
    public final boolean isCapturingKeys() {
        return keyboardModule.isCapturingKeys();
    }

    @LuaFunction
    public final boolean isCapturingMouse() {
        return keyboardModule.isCapturingMouse();
    }

    @LuaFunction
    public final void setCaptureMouse(boolean enable) {
        keyboardModule.setCaptureMouse(enable);
    }

    @LuaFunction
    public final boolean isHandlingInteraction(int button) throws LuaException {
        if (button < 1 || 3 < button) {
            throw new LuaException("argument #1 must in range of [1, 3]");
        }
        int mask = 1 << (button - 1);
        int buttons = this.access.getComputer().getModuleData(APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), (byte) 0);
        return (mask & buttons) != 0;
    }

    @LuaFunction
    public final void setHandlingInteraction(int button, boolean value) throws LuaException {
        if (button < 1 || 3 < button) {
            throw new LuaException("argument #1 must in range of [1, 3]");
        }
        int mask = 1 << (button - 1);
        int buttons = this.access.getComputer().getModuleData(APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), (byte) 0);
        if (value) {
            buttons |= mask;
        } else {
            buttons &= ~mask;
        }
        this.access.getComputer().setModuleData(APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), (byte) buttons);
    }
}
