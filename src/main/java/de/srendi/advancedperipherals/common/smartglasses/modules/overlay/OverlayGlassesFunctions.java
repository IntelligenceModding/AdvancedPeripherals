package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.util.Pair;

public class OverlayGlassesFunctions implements IModuleFunctions {

    private final OverlayModule overlayModule;
    private final SmartGlassesAccess access;

    public OverlayGlassesFunctions(OverlayModule overlayModule) {
        this.overlayModule = overlayModule;
        this.access = overlayModule.access;
    }

    @LuaFunction
    public final String test() {
        return "Hello World! I'm an overlay module!";
    }

    @LuaFunction
    public final MethodResult createPanel(String id, IArguments arguments) throws LuaException {
        Panel panel = new Panel(id, overlayModule, arguments);
        Pair<OverlayObject, Boolean> success = overlayModule.addObject(panel);
        if(!success.getRight())
            return MethodResult.of(success.getLeft(), IModule.ErrorConstants.ALREADY_EXISTS);

        return MethodResult.of(success.getLeft(), "SUCCESS");
    }

    @LuaFunction
    public final MethodResult getObjects(IArguments arguments) {
        return MethodResult.of((Object) overlayModule.getObjects().stream().map(OverlayObject::getId).toArray(String[]::new));
    }

}
