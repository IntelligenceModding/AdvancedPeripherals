package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static de.srendi.advancedperipherals.client.ClientRegistry.SADDLE_TURTLE_OVERLAY;

public class SaddleTurtleInfoPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<SaddleTurtleInfoPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("saddleturtleinfo"));

    private final int fuelLevel;
    private final int fuelLimit;
    private final int barColor;

    public SaddleTurtleInfoPacket(int fuelLevel, int fuelLimit, int barColor) {
        this.fuelLevel = fuelLevel;
        this.fuelLimit = fuelLimit;
        this.barColor = barColor;
    }

    public SaddleTurtleInfoPacket(RegistryFriendlyByteBuf buffer) {
        this.fuelLevel = buffer.readInt();
        this.fuelLimit = buffer.readInt();
        this.barColor = buffer.readInt();
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        SADDLE_TURTLE_OVERLAY.setFuelLevel(this.fuelLevel);
        SADDLE_TURTLE_OVERLAY.setFuelLimit(this.fuelLimit);
        SADDLE_TURTLE_OVERLAY.setBarColor(this.barColor);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.fuelLevel);
        buffer.writeInt(this.fuelLimit);
        buffer.writeInt(this.barColor);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
