package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BoxRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BoxObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 4;

    private static final BoxRenderer RENDERER = new BoxRenderer();

    @FloatingNumberProperty
    public float sizeX = 1;

    @FloatingNumberProperty
    public float sizeY = 1;

    @FloatingNumberProperty
    public float sizeZ = 1;

    public BoxObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
    }

    public BoxObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "box";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
