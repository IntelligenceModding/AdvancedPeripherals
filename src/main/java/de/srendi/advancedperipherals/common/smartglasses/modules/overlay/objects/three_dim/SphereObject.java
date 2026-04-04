package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.SphereRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SphereObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 6;

    private static final SphereRenderer RENDERER = new SphereRenderer();

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sectors = 16;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int stacks = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float radius = 1;

    public SphereObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
    }

    public SphereObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "sphere";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(sectors);
        buffer.writeInt(stacks);
        buffer.writeFloat(radius);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.sectors = buffer.readInt();
        this.stacks = buffer.readInt();
        this.radius = buffer.readFloat();
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
