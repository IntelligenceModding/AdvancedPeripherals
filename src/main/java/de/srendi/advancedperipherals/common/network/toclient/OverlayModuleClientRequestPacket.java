package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.addons.minecolonies.MineColonies;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OverlayModuleClientRequestPacket implements IPacket {

    public OverlayModuleClientRequestPacket() {

    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();

        int sizeX = minecraft.getWindow().getWidth(), sizeY = minecraft.getWindow().getHeight();
        double guiScale = minecraft.getWindow().getGuiScale();

        APNetworking.sendToServer(new OverlayModuleClientInfoPacket(minecraft.player.getUUID(), sizeX, sizeY, guiScale));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {

    }

    public static OverlayModuleClientRequestPacket decode(FriendlyByteBuf buffer) {
        return new OverlayModuleClientRequestPacket();
    }
}
