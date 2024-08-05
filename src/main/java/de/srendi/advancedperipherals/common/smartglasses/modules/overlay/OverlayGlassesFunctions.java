package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Circle;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Panel;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.client.Minecraft;

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
        Pair<RenderableObject, Boolean> success = overlayModule.addObject(panel);
        if(!success.getRight())
            return MethodResult.of(success.getLeft(), IModule.ErrorConstants.ALREADY_EXISTS);

        return MethodResult.of(success.getLeft(), "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createCircle(String id, IArguments arguments) throws LuaException {
        Circle circle = new Circle(id, overlayModule, arguments);
        Pair<RenderableObject, Boolean> success = overlayModule.addObject(circle);
        if(!success.getRight())
            return MethodResult.of(success.getLeft(), IModule.ErrorConstants.ALREADY_EXISTS);

        return MethodResult.of(success.getLeft(), "SUCCESS");
    }

    @LuaFunction
    public final MethodResult getObjects() {
        return MethodResult.of((Object) overlayModule.getObjects().stream().map(OverlayObject::getId).toArray(String[]::new));
    }

    @LuaFunction
    public final MethodResult getObject(IArguments arguments) throws LuaException {
        String id = arguments.getString(0);
        return MethodResult.of(overlayModule.getObjects().stream().filter(object -> object.getId().equals(id)).findFirst());
    }

    @LuaFunction
    public final MethodResult removeObject(String id) {
        return MethodResult.of(overlayModule.removeObject(id));
    }

    @LuaFunction
    public final MethodResult clear() {
        return MethodResult.of(overlayModule.clear());
    }

    // TODO: This will crash on dedicated servers
    @LuaFunction
    public final MethodResult getSize() {
        return MethodResult.of(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
    }

    @LuaFunction
    public final MethodResult update() {
        return MethodResult.of(overlayModule.bulkUpdate());
    }

    @LuaFunction
    public final MethodResult autoUpdate(IArguments arguments) throws LuaException {
        overlayModule.autoUpdate = arguments.optBoolean(0, !overlayModule.autoUpdate);
        return MethodResult.of(overlayModule.autoUpdate);
    }


}
