package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GlassesHotkeyPacket implements IAPPacket {

    public static final Type<GlassesHotkeyPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("glasses_hotkey"));

    private final String keyBind;
    private final int keyPressDuration;

    public GlassesHotkeyPacket(String keyBind, int keyPressDuration) {
        this.keyBind = keyBind;
        this.keyPressDuration = keyPressDuration;
    }

    public GlassesHotkeyPacket(RegistryFriendlyByteBuf buffer) {
        this.keyBind = buffer.readUtf();
        this.keyPressDuration = buffer.readInt();
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
        SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(player.server, smartGlasses);
        if (computer == null) {
            return;
        }
        if (keyPressDuration >= 0) {
            computer.queueEvent("glasses_key_pressed", new Object[]{keyBind, keyPressDuration});
            return;
        }
        SmartGlassesSideAccess glasses = computer.getSmartGlassesModuleAccess();
        KeyboardModule keyboardModule = computer.getModule(KeyboardModule.class);
        if (keyboardModule != null) {
            keyboardModule.openKeyboard(glasses);
        }
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(keyBind);
        buffer.writeInt(keyPressDuration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
