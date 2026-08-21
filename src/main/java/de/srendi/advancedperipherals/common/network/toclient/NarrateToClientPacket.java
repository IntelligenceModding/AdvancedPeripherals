package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.NarratorUtil;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class NarrateToClientPacket implements IAPPacket {
    private final String message;
    private final boolean interrupt;
    private final BlockPos source;

    public NarrateToClientPacket(String message, boolean interrupt, BlockPos source) {
        this.message = message;
        this.interrupt = interrupt;
        this.source = source;
    }

    public NarrateToClientPacket(FriendlyByteBuf buffer) {
        this.message = buffer.readUtf();
        this.interrupt = buffer.readBoolean();
        this.source = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.message);
        buffer.writeBoolean(this.interrupt);
        buffer.writeBlockPos(this.source);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // TODO: limit source range on client side?
        NarratorUtil.say(this.message, this.interrupt);
    }
}
