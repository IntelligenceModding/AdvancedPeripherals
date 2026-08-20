package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

public class HotkeyModuleItem extends BaseItem implements IModuleItem<HotkeyModule> {
    private Map<LivingEntity, Integer> clientKeyPressDuration = new WeakHashMap<>(); // client-only

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ResourceLocation moduleId() {
        return HotkeyModule.ID;
    }

    @Override
    @NotNull
    public HotkeyModule createModule(SmartGlassesSideAccess access) {
        return new HotkeyModule();
    }

    @Override
    public void moduleTick(Level level, LivingEntity entity, int moduleSlot, SmartGlassesSideAccess access, HotkeyModule module) {
        if (!level.isClientSide() || !(entity instanceof LocalPlayer)) {
            return;
        }

        if (KeybindUtil.isKeyPressed(KeyBindings.GLASSES_HOTKEY_KEYBINDING)) {
            // Add another 50ms to the duration, one tick
            this.clientKeyPressDuration.compute(entity, (e, v) -> (v == null ? 0 : v.intValue()) + 50);
            return;
        }
        int duration = this.clientKeyPressDuration.getOrDefault(entity, 0);
        // If the key is not pressed, but the duration is greater than 0, we can assume that the key was pressed
        // We can now post the event
        if (duration <= 0) {
            return;
        }
        this.clientKeyPressDuration.remove(entity);

        String keyBind = KeyBindings.GLASSES_HOTKEY_KEYBINDING.getKey().getName();
        APNetworking.sendToServer(new GlassesHotkeyPacket(keyBind, duration));
    }
}
