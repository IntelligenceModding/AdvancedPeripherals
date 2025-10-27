package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RenderableObjectBulkSyncPacket implements IAPPacket {

    public static final Type<RenderableObjectBulkSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderableobjectbulksync"));

    private final Collection<RenderableObject> objects;

    public RenderableObjectBulkSyncPacket(Collection<RenderableObject> objects) {
        this.objects = objects;
    }

    public RenderableObjectBulkSyncPacket(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<RenderableObject> objects = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int typeId = buffer.readInt();
            objects.add(ObjectDecodeRegistry.getObject(typeId, buffer));
        }
        this.objects = objects;
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.addOrUpdateObjects(objects);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(objects.size());
        for (RenderableObject object : objects)
            object.encode(buffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
