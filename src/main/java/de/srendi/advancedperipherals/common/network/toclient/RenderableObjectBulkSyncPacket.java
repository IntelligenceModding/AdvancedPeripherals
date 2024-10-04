package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RenderableObjectBulkSyncPacket implements IPacket {

    private final Collection<RenderableObject> objects;

    public RenderableObjectBulkSyncPacket(Collection<RenderableObject> objects) {
        this.objects = objects;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.addOrUpdateObjects(objects);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(objects.size());
        for (RenderableObject object : objects)
            object.encode(buffer);
    }

    public static RenderableObjectBulkSyncPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<RenderableObject> objects = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int typeId = buffer.readInt();
            objects.add(ObjectDecodeRegistry.getObject(typeId, buffer));
        }

        return new RenderableObjectBulkSyncPacket(objects);
    }
}
