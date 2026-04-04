package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public float opacity = 1;

    @FixedPointNumberProperty(min = 0, max = 0xffffff)
    public int color = 0xffffff;

    @FloatingNumberProperty
    public float x = 0;

    @FloatingNumberProperty
    public float y = 0;

    @FloatingNumberProperty
    public float z = 0;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotX = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotY = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotZ = 0f;

    public RenderableObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    // TODO: switch to Registry
    public abstract int getTypeId();

    @Override
    public void tryAutoUpdate() {
        this.getModule().update(this);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeFloat(opacity);

        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(rotX);
        buffer.writeFloat(rotY);
        buffer.writeFloat(rotZ);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.color = buffer.readInt();
        this.opacity = buffer.readFloat();

        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.z = buffer.readFloat();
        this.rotX = buffer.readFloat();
        this.rotY = buffer.readFloat();
        this.rotZ = buffer.readFloat();
    }

    public abstract IObjectRenderer getObjectRenderer();

    @Override
    public String toString() {
        return "RenderableObject{" +
                "opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
