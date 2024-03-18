package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.network.PacketHandler;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HotkeyModuleItem extends BaseItem implements IModuleItem {

    private static final String KEY_PRESS_DURATION_NBT = "KeyPressDuration";

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IModule createModule(SmartGlassesAccess access) {
        return new HotkeyModule();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide() || !(entity instanceof Player player))
            return;

        if (KeybindUtil.isKeyPressed(KeyBindings.GLASSES_HOTKEY_KEYBINDING)) {
            // Add another 50ms to the duration, one tick
            setKeyPressDuration(stack, getKeyPressDuration(stack) + 50);
        } else if(getKeyPressDuration(stack) > 0) {
            // If the key is not pressed, but the duration is greater than 0, we can assume that the key was pressed
            // We can now post the event

            int duration = getKeyPressDuration(stack);
            setKeyPressDuration(stack, 0);

            String keyBind = KeyBindings.GLASSES_HOTKEY_KEYBINDING.getKey().getName();
            PacketHandler.sendToServer(new GlassesHotkeyPacket(player.getUUID(), keyBind, duration));
        }
    }

    public static int getKeyPressDuration(ItemStack stack) {
        return stack.copy().getOrCreateTag().getInt(KEY_PRESS_DURATION_NBT);
    }

    public static void setKeyPressDuration(ItemStack stack, int keyPressDuration) {
        stack.getOrCreateTag().putInt(KEY_PRESS_DURATION_NBT, keyPressDuration);
    }
}
