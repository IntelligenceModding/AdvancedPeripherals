package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.world.phys.Vec3;

public class OverlayGlassesFunctions implements IModuleFunctions {

    private final OverlayModule overlayModule;
    private final SmartGlassesSideAccess access;

    public OverlayGlassesFunctions(OverlayModule overlayModule, SmartGlassesSideAccess access) {
        this.overlayModule = overlayModule;
        this.access = access;
    }

    @LuaFunction
    public final Object createRectangle(IArguments arguments) throws LuaException {
        RectangleObject rectangle = new RectangleObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(rectangle);

        return object;
    }

    @LuaFunction
    public final Object createCircle(IArguments arguments) throws LuaException {
        CircleObject circle = new CircleObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(circle);

        return object;
    }

    @LuaFunction
    public final Object createLine(IArguments arguments) throws LuaException {
        LineObject rectangle = new LineObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(rectangle);

        return object;
    }

    @LuaFunction
    public final Object createText(IArguments arguments) throws LuaException {
        TextObject circle = new TextObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(circle);

        return object;
    }

    @LuaFunction
    public final Object createItem(IArguments arguments) throws LuaException {
        ItemObject item = new ItemObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(item);

        return object;
    }

    @LuaFunction
    public final Object createBlock(IArguments arguments) throws LuaException {
        BlockObject block = new BlockObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return object;
    }

    @LuaFunction
    public final Object createBox(IArguments arguments) throws LuaException {
        BoxObject block = new BoxObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return object;
    }

    @LuaFunction
    public final Object createSphere(IArguments arguments) throws LuaException {
        SphereObject block = new SphereObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return object;
    }

    @LuaFunction
    public final Object createTorus(IArguments arguments) throws LuaException {
        TorusObject block = new TorusObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return object;
    }

    @LuaFunction
    public final MethodResult getObject(IArguments arguments) throws LuaException {
        int id = arguments.getInt(0);
        return MethodResult.of(overlayModule.getObjects().get(id));
    }

    @LuaFunction
    public final boolean removeObject(int id) {
        return overlayModule.removeObject(id);
    }

    @LuaFunction
    public final int clear() {
        return overlayModule.clear();
    }

    @LuaFunction
    public final int getObjectsSize() {
        return overlayModule.getObjects().size();
    }

    @LuaFunction
    public final MethodResult getSize() {
        return MethodResult.of(overlayModule.getScreenWidth(), overlayModule.getScreenHeight());
    }

    @LuaFunction
    public final double getGuiScale() {
        return overlayModule.getGuiScale();
    }

    @LuaFunction
    public final MethodResult getEyePosition() {
        Vec3 pos = access.getEntity().getEyePosition();
        return MethodResult.of(pos.x, pos.y, pos.z);
    }

    @LuaFunction
    public final int update() {
        return overlayModule.bulkUpdate();
    }

    @LuaFunction
    public final boolean autoUpdatde() {
        return overlayModule.autoUpdate;
    }

    @LuaFunction
    public final void autoUpdate(boolean autoUpdate) {
        overlayModule.autoUpdate = autoUpdate;
    }
}
