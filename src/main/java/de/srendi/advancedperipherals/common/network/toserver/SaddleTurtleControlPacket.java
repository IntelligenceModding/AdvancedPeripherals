package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.util.InputKeySet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SaddleTurtleControlPacket implements IAPPacket {

    public final InputKeySet inputs;

    public SaddleTurtleControlPacket(InputKeySet inputs) {
        this.inputs = inputs;
    }

    public SaddleTurtleControlPacket(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        this(new InputKeySet(forward, back, left, right, up, down));
    }

    public SaddleTurtleControlPacket(FriendlyByteBuf buffer) {
        this(InputKeySet.fromByte(buffer.readByte()));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null && player.getRootVehicle() instanceof TurtleSeatEntity seat) {
            seat.handleSaddleTurtleControlPacket(this);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(this.inputs.toByte());
    }
}
