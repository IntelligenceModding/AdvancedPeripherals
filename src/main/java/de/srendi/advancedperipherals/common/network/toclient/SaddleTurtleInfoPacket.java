package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import static de.srendi.advancedperipherals.client.ClientRegistry.SADDLE_TURTLE_OVERLAY;

public class SaddleTurtleInfoPacket implements IPacket {

    private final int fuelLevel;
    private final int fuelLimit;
    private final int barColor;

    public SaddleTurtleInfoPacket(int fuelLevel, int fuelLimit, int barColor) {
        this.fuelLevel = fuelLevel;
        this.fuelLimit = fuelLimit;
        this.barColor = barColor;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        SADDLE_TURTLE_OVERLAY.setFuelLevel(this.fuelLevel);
        SADDLE_TURTLE_OVERLAY.setFuelLimit(this.fuelLimit);
        SADDLE_TURTLE_OVERLAY.setBarColor(this.barColor);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.fuelLevel);
        buffer.writeInt(this.fuelLimit);
        buffer.writeInt(this.barColor);
    }

    public static SaddleTurtleInfoPacket decode(FriendlyByteBuf buffer) {
        int fuelLevel = buffer.readInt();
        int fuelLimit = buffer.readInt();
        int barColor = buffer.readInt();
        return new SaddleTurtleInfoPacket(fuelLevel, fuelLimit, barColor);
    }
}
