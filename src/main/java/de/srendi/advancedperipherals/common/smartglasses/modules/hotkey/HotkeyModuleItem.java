package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class HotkeyModuleItem extends BaseItem implements IModuleItem {

    private static final String KEY_PRESS_DURATION_NBT = "KeyPressDuration";

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IModule createModule(SmartGlassesAccess access, ItemStack stack) {
        return new HotkeyModule();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide() || !(entity instanceof LocalPlayer)) {
            return;
        }

        if (KeybindUtil.isKeyPressed(KeyBindings.GLASSES_HOTKEY_KEYBINDING)) {
            // Add another 50ms to the duration, one tick
            setKeyPressDuration(stack, getKeyPressDuration(stack) + 50);
            return;
        }
        int duration = getKeyPressDuration(stack);
        // If the key is not pressed, but the duration is greater than 0, we can assume that the key was pressed
        // We can now post the event
        if (duration > 0) {
            setKeyPressDuration(stack, 0);

            String keyBind = KeyBindings.GLASSES_HOTKEY_KEYBINDING.getKey().getName();
            PacketDistributor.sendToServer(new GlassesHotkeyPacket(keyBind, duration));
        }
    }

    public static int getKeyPressDuration(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(KEY_PRESS_DURATION_NBT) : 0;
    }

    public static void setKeyPressDuration(ItemStack stack, int keyPressDuration) {
        stack.getOrCreateTag().putInt(KEY_PRESS_DURATION_NBT, keyPressDuration);
    }
}
