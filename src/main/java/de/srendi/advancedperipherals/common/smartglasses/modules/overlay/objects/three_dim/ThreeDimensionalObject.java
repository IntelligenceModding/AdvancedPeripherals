package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class ThreeDimensionalObject extends RenderableObject {

    @BooleanProperty(getterPrefix = "has")
    public boolean depthTest = true;

    @BooleanProperty
    public boolean culling = true;

    public ThreeDimensionalObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
    }

    public ThreeDimensionalObject(UUID player) {
        super(player);
    }

    @Override
    public abstract IThreeDObjectRenderer<?> getObjectRenderer();

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeBoolean(depthTest);
        buffer.writeBoolean(culling);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.depthTest = buffer.readBoolean();
        this.culling = buffer.readBoolean();
    }
}
