package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class GlassesHotkeyPacket implements IPacket {

    private final String keyBind;
    private final int keyPressDuration;

    public GlassesHotkeyPacket(String keyBind, int keyPressDuration) {
        this.keyBind = keyBind;
        this.keyPressDuration = keyPressDuration;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ServerPlayer serverPlayer = context.getSender();
        if (serverPlayer == null) {
            return;
        }

        ItemStack smartGlasses = null;
        SmartGlassesComputer computer = null;
        for (ItemStack stack : serverPlayer.getAllSlots()) {
            if (stack.getItem() instanceof SmartGlassesItem) {
                computer = SmartGlassesItem.getServerComputer(server, stack);
                if (computer != null) {
                    smartGlasses = stack;
                    break;
                }
            }
        }
        if (computer == null) {
            return;
        }
        if (keyPressDuration >= 0) {
            computer.queueEvent("glasses_key_pressed", new Object[]{keyBind, keyPressDuration});
            return;
        }
        SmartGlassesAccess glasses = computer.getSmartGlassesAccess();
        computer.getModules().values()
            .stream()
            .filter(KeyboardModule.class::isInstance)
            .map(KeyboardModule.class::cast)
            .findFirst()
            .ifPresent((keyboardModule) -> {
                keyboardModule.openKeyboard(glasses);
            });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(keyBind);
        buffer.writeInt(keyPressDuration);
    }

    public static GlassesHotkeyPacket decode(FriendlyByteBuf buffer) {
        return new GlassesHotkeyPacket(buffer.readUtf(), buffer.readInt());
    }
}
