package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectBulkSyncPacket implements IPacket {

    private final RenderableObject[] objects;

    public RenderableObjectBulkSyncPacket(RenderableObject[] objects) {
        this.objects = objects;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.addOrUpdateObjects(objects);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(objects.length);
        for (RenderableObject object : objects)
            object.encode(buffer);
    }

    public static RenderableObjectBulkSyncPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        RenderableObject[] objects = new RenderableObject[size];

        for (int i = 0; i < size; i++) {
            int id = buffer.readInt();
            objects[i] = ObjectDecodeRegistry.getObject(id, buffer);
        }

        return new RenderableObjectBulkSyncPacket(objects);
    }
}
