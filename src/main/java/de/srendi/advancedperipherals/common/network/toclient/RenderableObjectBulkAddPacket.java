package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RenderableObjectBulkAddPacket implements IAPPacket {

    private final UUID player;
    private final Collection<OverlayObject> objects;

    public RenderableObjectBulkAddPacket(UUID player, Collection<OverlayObject> objects) {
        this.player = player;
        this.objects = objects;
    }

    public RenderableObjectBulkAddPacket(FriendlyByteBuf buffer) {
        IForgeRegistry<OverlayObjectType<?>> registry = APRegistration.OVERLAY_OBJECTS_REG.get();
        this.player = buffer.readUUID();
        int size = buffer.readVarInt();
        List<OverlayObject> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation typeId = buffer.readResourceLocation();
            OverlayObject object = Objects.requireNonNull(registry.getValue(typeId)).createClient(this.player);
            object.decode(buffer);
            objects.add(object);
        }
        this.objects = objects;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.putObjects(this.objects);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        IForgeRegistry<OverlayObjectType<?>> registry = APRegistration.OVERLAY_OBJECTS_REG.get();
        buffer.writeUUID(this.player);
        buffer.writeVarInt(this.objects.size());
        for (OverlayObject object : this.objects) {
            buffer.writeResourceLocation(registry.getKey(object.getType()));
            object.encode(buffer);
        }
    }
}
