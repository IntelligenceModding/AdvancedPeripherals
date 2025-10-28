package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.util.InputKeySet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SaddleTurtleControlPacket implements IPacket {
    public final InputKeySet inputs;

    public SaddleTurtleControlPacket(InputKeySet inputs) {
        this.inputs = inputs;
    }

    public SaddleTurtleControlPacket(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        this(new InputKeySet(forward, back, left, right, up, down));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.getRootVehicle() instanceof TurtleSeatEntity seat) {
            seat.handleSaddleTurtleControlPacket(this);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeByte(this.inputs.toBits());
    }

    public static SaddleTurtleControlPacket decode(FriendlyByteBuf buffer) {
        return new SaddleTurtleControlPacket(InputKeySet.fromByte(buffer.readByte()));
    }
}
