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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class RenderableObjectBulkAddPacket implements IAPPacket {

    private final UUID player;
    private final Collection<OverlayObject> objects;

    public RenderableObjectBulkAddPacket(UUID player, Collection<OverlayObject> objects) {
        this.player = player;
        this.objects = objects;
    }

    public RenderableObjectBulkAddPacket(FriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = (Registry<OverlayObjectType<?>>) BuiltInRegistries.REGISTRY.getOrThrow(APRegistries.OVERLAY_OBJECTS);;
        this.player = buffer.readUUID();
        int size = buffer.readVarInt();
        List<OverlayObject> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int typeId = buffer.readVarInt();
            OverlayObject object = registry.byIdOrThrow(typeId).createClient(this.player);
            object.decode(buffer);
            objects.add(object);
        }
        this.objects = objects;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        OverlayObjectHolder.putObjects(this.objects);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = (Registry<OverlayObjectType<?>>) BuiltInRegistries.REGISTRY.getOrThrow(APRegistries.OVERLAY_OBJECTS);
        buffer.writeUUID(this.player);
        buffer.writeVarInt(this.objects.size());
        for (OverlayObject object : this.objects) {
            buffer.writeVarInt(registry.getId(object.getType()));
            object.encode(buffer);
        }
    }
}
