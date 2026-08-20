package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static de.srendi.advancedperipherals.client.ClientRegistry.SADDLE_TURTLE_OVERLAY;

public class SaddleTurtleInfoPacket implements IAPPacket {

    private final int fuelLevel;
    private final int fuelLimit;
    private final int barColor;

    public SaddleTurtleInfoPacket(int fuelLevel, int fuelLimit, int barColor) {
        this.fuelLevel = fuelLevel;
        this.fuelLimit = fuelLimit;
        this.barColor = barColor;
    }

    public SaddleTurtleInfoPacket(FriendlyByteBuf buffer) {
        this.fuelLevel = buffer.readInt();
        this.fuelLimit = buffer.readInt();
        this.barColor = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        SADDLE_TURTLE_OVERLAY.setFuelLevel(this.fuelLevel);
        SADDLE_TURTLE_OVERLAY.setFuelLimit(this.fuelLimit);
        SADDLE_TURTLE_OVERLAY.setBarColor(this.barColor);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.fuelLevel);
        buffer.writeInt(this.fuelLimit);
        buffer.writeInt(this.barColor);
    }
}
