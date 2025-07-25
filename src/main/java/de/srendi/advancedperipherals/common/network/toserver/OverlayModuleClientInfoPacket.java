package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class OverlayModuleClientInfoPacket implements IPacket {

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

    @Override
    public void handle(NetworkEvent.Context context) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player);
        if (serverPlayer == null)
            return;

        for (ItemStack stack : serverPlayer.getAllSlots()) {
            if (stack.getItem() instanceof SmartGlassesItem) {
                SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(server, stack);

                if (computer != null) {
                    OverlayModule module = computer.getModule(OverlayModule.class);

                    if (module != null)
                        module.setScreenSizes(screenWidth, screenHeight, guiScale);
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player);
        buffer.writeInt(screenWidth);
        buffer.writeInt(screenHeight);
        buffer.writeDouble(guiScale);
    }

    public static OverlayModuleClientInfoPacket decode(FriendlyByteBuf buffer) {
        return new OverlayModuleClientInfoPacket(buffer.readUUID(), buffer.readInt(), buffer.readInt(), buffer.readDouble());
    }
}
