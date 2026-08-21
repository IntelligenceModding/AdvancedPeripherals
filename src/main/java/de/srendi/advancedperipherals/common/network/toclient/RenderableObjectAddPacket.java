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

import java.util.Objects;
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
        IForgeRegistry<OverlayObjectType<?>> registry = APRegistration.OVERLAY_OBJECTS_REG.get();
        this.player = buffer.readUUID();
        ResourceLocation typeId = buffer.readResourceLocation();
        this.object = Objects.requireNonNull(registry.getValue(typeId)).createClient(this.player);
        this.object.decode(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        OverlayObjectHolder.putObject(this.object);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        IForgeRegistry<OverlayObjectType<?>> registry = APRegistration.OVERLAY_OBJECTS_REG.get();
        buffer.writeUUID(this.player);
        buffer.writeResourceLocation(registry.getKey(this.object.getType()));
        object.encode(buffer);
    }
}
