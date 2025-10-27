package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SaddleTurtleControlPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<SaddleTurtleControlPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("saddleturtlecontrol"));

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

    public SaddleTurtleControlPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readByte());
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
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
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(this.encodeToBits());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
