package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class GlassesHotkeyPacket implements IAPPacket {
    public static final GlassesHotkeyPacket KEYBOARD_OPEN_PACKET = new GlassesHotkeyPacket("", -1);

    private final String keyBind;
    private final int keyPressDuration;

    public GlassesHotkeyPacket(String keyBind, int keyPressDuration) {
        this.keyBind = keyBind;
        this.keyPressDuration = keyPressDuration;
    }

    public GlassesHotkeyPacket(FriendlyByteBuf buffer) {
        this.keyBind = buffer.readUtf();
        this.keyPressDuration = buffer.readInt();
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
        if (keyPressDuration >= 0) {
            computer.queueEvent(CCEvents.GLASSES_KEY_PRESSED, new Object[]{keyBind, keyPressDuration});
            return;
        }
        SmartGlassesSideAccess glasses = computer.getSmartGlassesModuleAccess();
        KeyboardModule keyboardModule = computer.getModule(KeyboardModule.class);
        if (keyboardModule != null) {
            keyboardModule.openKeyboard(glasses);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(keyBind);
        buffer.writeInt(keyPressDuration);
    }
}
