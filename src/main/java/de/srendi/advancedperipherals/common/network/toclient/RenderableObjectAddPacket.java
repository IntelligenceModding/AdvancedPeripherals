package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class RenderableObjectAddPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectAddPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderable_object_add"));

    private final UUID player;
    private final OverlayObject object;

    public RenderableObjectAddPacket(UUID player, OverlayObject object) {
        this.player = player;
        this.object = object;
    }

    public RenderableObjectAddPacket(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        this.player = buffer.readUUID();
        int typeId = buffer.readVarInt();
        this.object = registry.byIdOrThrow(typeId).createClient(this.player);
        this.object.decode(buffer);
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.putObject(this.object);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        buffer.writeUUID(this.player);
        buffer.writeVarInt(registry.getIdOrThrow(this.object.getType()));
        object.encode(buffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
