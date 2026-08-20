package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RenderableObjectAddPacket implements IAPPacket {
    private final UUID player;
    private final OverlayObject object;

    public RenderableObjectAddPacket(UUID player, OverlayObject object) {
        this.player = player;
        this.object = object;
    }

    public RenderableObjectAddPacket(FriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = (Registry<OverlayObjectType<?>>) BuiltInRegistries.REGISTRY.getOrThrow(APRegistries.OVERLAY_OBJECTS);
        this.player = buffer.readUUID();
        int typeId = buffer.readVarInt();
        this.object = registry.byIdOrThrow(typeId).createClient(this.player);
        this.object.decode(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        OverlayObjectHolder.putObject(this.object);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = (Registry<OverlayObjectType<?>>) BuiltInRegistries.REGISTRY.getOrThrow(APRegistries.OVERLAY_OBJECTS);
        buffer.writeUUID(this.player);
        buffer.writeVarInt(registry.getId(this.object.getType()));
        object.encode(buffer);
    }
}
