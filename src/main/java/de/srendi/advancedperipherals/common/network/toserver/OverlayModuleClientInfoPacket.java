package de.srendi.advancedperipherals.common.network.toserver;

import com.mojang.blaze3d.platform.Window;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class OverlayModuleClientInfoPacket implements IAPPacket {

    private final UUID player;
    private final int screenWidth;
    private final int screenHeight;
    private final double guiScale;

    public OverlayModuleClientInfoPacket(UUID player, int screenWidth, int screenHeight, double guiScale) {
        this.player = player;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.guiScale = guiScale;
    }

    public OverlayModuleClientInfoPacket(FriendlyByteBuf buffer) {
        this.player = buffer.readUUID();
        this.screenWidth = buffer.readInt();
        this.screenHeight = buffer.readInt();
        this.guiScale = buffer.readDouble();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();

        ItemStack smartGlasses = SmartGlassesItem.getEquipped(player);
        if (smartGlasses.isEmpty()) {
            return;
        }
        SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(player.serverLevel().getServer(), smartGlasses);
        if (computer == null) {
            return;
        }
        OverlayModule module = computer.getModule(OverlayModule.class);
        if (module == null) {
            return;
        }
        module.setScreenSizes(screenWidth, screenHeight, guiScale);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(player);
        buffer.writeInt(screenWidth);
        buffer.writeInt(screenHeight);
        buffer.writeDouble(guiScale);
    }

    public static void sendCurrentInformation() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        Window window = minecraft.getWindow();

        int sizeX = window.getWidth(), sizeY = window.getHeight();
        double guiScale = window.getGuiScale();

        APNetworking.sendToServer(new OverlayModuleClientInfoPacket(minecraft.player.getUUID(), sizeX, sizeY, guiScale));
    }
}
