package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.util.InputKeySet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SaddleTurtleControlPacket implements IAPPacket {

    public static final Type<SaddleTurtleControlPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("saddle_turtle_control"));

    public final InputKeySet inputs;

    public SaddleTurtleControlPacket(InputKeySet inputs) {
        this.inputs = inputs;
    }

    public SaddleTurtleControlPacket(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        this(new InputKeySet(forward, back, left, right, up, down));
    }

    public SaddleTurtleControlPacket(RegistryFriendlyByteBuf buffer) {
        this(InputKeySet.fromByte(buffer.readByte()));
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        if (player != null && player.getRootVehicle() instanceof TurtleSeatEntity seat) {
            seat.handleSaddleTurtleControlPacket(this);
        }
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(this.inputs.toByte());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
