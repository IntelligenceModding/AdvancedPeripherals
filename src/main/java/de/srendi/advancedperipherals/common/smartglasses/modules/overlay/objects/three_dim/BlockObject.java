package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BlockRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class BlockObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 5;

    private static final IThreeDObjectRenderer RENDERER = new BlockRenderer();

    @StringProperty
    public String block = "minecraft:air";

    public BlockObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public BlockObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setBlock(String block) {
        this.block = block;
        getModule().update(this);
    }

    @LuaFunction
    public final String getBlock() {
        return block;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(block);
    }

    public static BlockObject decode(FriendlyByteBuf buffer) {
        Optional<BlockObject> optionalObject = RenderableObject.baseDecode(buffer, BlockObject::new);
        if (optionalObject.isEmpty())
            return null;

        boolean disableDepthTest = buffer.readBoolean();
        boolean disableCulling = buffer.readBoolean();

        String block = buffer.readUtf();

        BlockObject clientObject = optionalObject.get();
        clientObject.disableDepthTest = disableDepthTest;
        clientObject.disableCulling = disableCulling;
        clientObject.block = block;

        return clientObject;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
