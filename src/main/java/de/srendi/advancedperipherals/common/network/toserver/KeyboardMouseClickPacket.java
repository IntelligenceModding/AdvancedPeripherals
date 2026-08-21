package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class KeyboardMouseClickPacket implements IAPPacket {

    private final int button;
    private final boolean isRelease;

    public KeyboardMouseClickPacket(int button, boolean isRelease) {
        this.button = button;
        this.isRelease = isRelease;
    }

    public KeyboardMouseClickPacket(FriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        this.isRelease = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();

        ItemStack smartGlasses = SmartGlassesItem.getEquipped(player);
        if (smartGlasses.isEmpty()) {
            return;
        }
        SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(player.server, smartGlasses);
        if (computer == null) {
            return;
        }
        computer.queueEvent(isRelease ? CCEvents.PLAYER_MOUSE_UP : CCEvents.PLAYER_MOUSE_CLICK, new Object[]{button});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        buffer.writeBoolean(isRelease);
    }
}
