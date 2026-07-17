package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.NarratorUtil;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class NarrateToClientPacket implements IAPPacket {
    public static final Type<NarrateToClientPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("narrate_to_client"));

    private final String message;
    private final boolean interrupt;
    private final BlockPos source;

    public NarrateToClientPacket(String message, boolean interrupt, BlockPos source) {
        this.message = message;
        this.interrupt = interrupt;
        this.source = source;
    }

    public NarrateToClientPacket(RegistryFriendlyByteBuf buffer) {
        this.message = buffer.readUtf();
        this.interrupt = buffer.readBoolean();
        this.source = buffer.readBlockPos();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(this.message);
        buffer.writeBoolean(this.interrupt);
        buffer.writeBlockPos(this.source);
    }

    @Override
    public void handle(@NotNull IPayloadContext context) {
        // TODO: limit source range on client side?
        NarratorUtil.say(this.message, this.interrupt);
    }

    @Override
    @NotNull
    public Type<NarrateToClientPacket> type() {
        return TYPE;
    }
}
