package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class OverlayModuleClientInfoPacket implements IAPPacket {

    public static final Type<OverlayModuleClientInfoPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("overlay_module_client_info"));

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

    public OverlayModuleClientInfoPacket(RegistryFriendlyByteBuf buffer) {
        this.player = buffer.readUUID();
        this.screenWidth = buffer.readInt();
        this.screenHeight = buffer.readInt();
        this.guiScale = buffer.readDouble();
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

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
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(player);
        buffer.writeInt(screenWidth);
        buffer.writeInt(screenHeight);
        buffer.writeDouble(guiScale);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
