package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SaddleTurtleControlPacket implements IPacket {

    public final boolean forward;
    public final boolean back;
    public final boolean left;
    public final boolean right;
    public final boolean up;
    public final boolean down;

    private static final byte FORWARD_BIT = 1 << 0;
    private static final byte BACK_BIT = 1 << 1;
    private static final byte LEFT_BIT = 1 << 2;
    private static final byte RIGHT_BIT = 1 << 3;
    private static final byte UP_BIT = 1 << 4;
    private static final byte DOWN_BIT = 1 << 5;

    public SaddleTurtleControlPacket(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        this.forward = forward;
        this.back = back;
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
    }

    private SaddleTurtleControlPacket(byte bits) {
        this((bits & FORWARD_BIT) != 0, (bits & BACK_BIT) != 0, (bits & LEFT_BIT) != 0, (bits & RIGHT_BIT) != 0, (bits & UP_BIT) != 0, (bits & DOWN_BIT) != 0);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.getRootVehicle() instanceof TurtleSeatEntity seat) {
            seat.handleSaddleTurtleControlPacket(this);
        }
    }

    private byte encodeToBits() {
        byte b = 0;
        if (this.forward) {
            b |= FORWARD_BIT;
        }
        if (this.back) {
            b |= BACK_BIT;
        }
        if (this.left) {
            b |= LEFT_BIT;
        }
        if (this.right) {
            b |= RIGHT_BIT;
        }
        if (this.up) {
            b |= UP_BIT;
        }
        if (this.down) {
            b |= DOWN_BIT;
        }
        return b;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeByte(this.encodeToBits());
    }

    public static SaddleTurtleControlPacket decode(FriendlyByteBuf buffer) {
        return new SaddleTurtleControlPacket(buffer.readByte());
    }
}
