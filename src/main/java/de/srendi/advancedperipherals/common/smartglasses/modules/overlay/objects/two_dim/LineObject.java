package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Just a line
 */
public class LineObject extends RenderableObject {
    @FloatingNumberProperty
    public float endX = 0;

    @FloatingNumberProperty
    public float endY = 0;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int width = 4;

    public LineObject(OverlayModule module) {
        super(module);
    }

    /**
     * constructor for the client side initialization
     *
     * @param player the target player
     */
    public LineObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<LineObject> getType() {
        return APOverlayObjects.LINE.get();
    }

    @LuaFunction
    public MethodResult getEndPos() {
        return MethodResult.of(this.endX, this.endY);
    }

    @LuaFunction
    public void setEndPos(double x, double y) {
        this.endX = (float) x;
        this.endY = (float) y;
        this.markAndTryUpdate("endX", "endY");
    }
}
