package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TriangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public class OverlayGlassesFunctions implements IModuleFunctions {

    private final OverlayModule overlayModule;
    private final SmartGlassesSideAccess access;

    public OverlayGlassesFunctions(OverlayModule overlayModule, SmartGlassesSideAccess access) {
        this.overlayModule = overlayModule;
        this.access = access;
    }

    @LuaFunction
    public final Object createObject(String type, Optional<Map<?, ?>> initFields) throws LuaException {
        Registry<OverlayObjectType<?>> registry = this.access.getLevel().registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        ResourceLocation id = ResourceLocation.tryParse(type);
        if (id == null) {
            throw new LuaException("Invalid overlay type '" + type + "'");
        }
        OverlayObjectType<?> typ = registry.get(id);
        if (typ == null) {
            throw new LuaException("Invalid overlay type '" + type + "'");
        }
        OverlayObject object = typ.createServer(overlayModule);
        object.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject((RenderableObject) object);

        return object;
    }

    @LuaFunction
    public final Object createRectangle(Optional<Map<?, ?>> initFields) throws LuaException {
        RectangleObject rectangle = new RectangleObject(overlayModule);
        rectangle.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(rectangle);
        return rectangle;
    }

    @LuaFunction
    public final Object createCircle(Optional<Map<?, ?>> initFields) throws LuaException {
        CircleObject circle = new CircleObject(overlayModule);
        circle.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(circle);
        return circle;
    }

    @LuaFunction
    public final Object createLine(Optional<Map<?, ?>> initFields) throws LuaException {
        LineObject line = new LineObject(overlayModule);
        line.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(line);
        return line;
    }

    @LuaFunction
    public final Object createText(Optional<Map<?, ?>> initFields) throws LuaException {
        TextObject text = new TextObject(overlayModule);
        text.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(text);
        return text;
    }

    @LuaFunction
    public final Object createItem(Optional<Map<?, ?>> initFields) throws LuaException {
        ItemObject item = new ItemObject(overlayModule);
        item.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(item);
        return item;
    }

    @LuaFunction
    public final Object createBlock(Optional<Map<?, ?>> initFields) throws LuaException {
        BlockObject block = new BlockObject(overlayModule);
        block.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(block);
        return block;
    }

    @LuaFunction
    public final Object createBox(Optional<Map<?, ?>> initFields) throws LuaException {
        BoxObject box = new BoxObject(overlayModule);
        box.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(box);
        return box;
    }

    @LuaFunction
    public final Object createSphere(Optional<Map<?, ?>> initFields) throws LuaException {
        SphereObject sphere = new SphereObject(overlayModule);
        sphere.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(sphere);
        return sphere;
    }

    @LuaFunction
    public final Object createTorus(Optional<Map<?, ?>> initFields) throws LuaException {
        TorusObject torus = new TorusObject(overlayModule);
        torus.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(torus);
        return torus;
    }

    @LuaFunction
    public final Object createTriangle(Optional<Map<?, ?>> initFields) throws LuaException {
        TriangleObject torus = new TriangleObject(overlayModule);
        torus.setPropertiesFromTable(EmptyLuaTable.orEmpty(initFields.orElse(null)));
        overlayModule.addObject(torus);
        return torus;
    }

    @LuaFunction
    public final MethodResult getObject(int id) throws LuaException {
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
    public final int getObjectsCount() {
        return overlayModule.getObjects().size();
    }

    @LuaFunction
    public final MethodResult getGuiSize() {
        return MethodResult.of(overlayModule.getScreenWidth(), overlayModule.getScreenHeight(), overlayModule.getGuiScale());
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
    public final boolean autoUpdate() {
        return overlayModule.autoUpdate;
    }

    @LuaFunction
    public final void setAutoUpdate(boolean autoUpdate) {
        overlayModule.autoUpdate = autoUpdate;
    }
}
