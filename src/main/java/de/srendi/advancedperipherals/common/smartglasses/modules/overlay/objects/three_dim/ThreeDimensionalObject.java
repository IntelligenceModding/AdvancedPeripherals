package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class ThreeDimensionalObject extends RenderableObject {

    @BooleanProperty
    public boolean disableDepthTest = false;

    @BooleanProperty
    public boolean disableCulling = false;

    public ThreeDimensionalObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public ThreeDimensionalObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setDepthTest(boolean depthTest) {
        disableDepthTest = depthTest;
        getModule().update(this);
    }

    @LuaFunction
    public final boolean getDepthTest() {
        return disableDepthTest;
    }

    @LuaFunction
    public final void setCulling(boolean culling) {
        disableCulling = culling;
        getModule().update(this);
    }

    @LuaFunction
    public final boolean getCulling() {
        return disableCulling;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);

        buffer.writeBoolean(disableDepthTest);
        buffer.writeBoolean(disableCulling);
    }

}
