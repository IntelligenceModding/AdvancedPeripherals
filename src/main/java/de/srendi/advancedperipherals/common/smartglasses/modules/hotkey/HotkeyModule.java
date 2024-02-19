package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import net.minecraft.resources.ResourceLocation;

public class HotkeyModule implements IModule {

    private int keyPressDuration = 0;

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "hotkey");
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return null;
    }

    @Override
    public void tick(SmartGlassesAccess smartGlassesAccess) {
        if (KeybindUtil.isKeyPressed(KeyBindings.GLASSES_HOTKEY_KEYBINDING)) {
            // Add another 50ms to the duration, one tick
            keyPressDuration = keyPressDuration + 50;
        } else if(keyPressDuration > 0) {
            // If the key is not pressed, but the duration is greater than 0, we can assume that the key was pressed
            // We can now post the event
            String keyBind = KeyBindings.GLASSES_HOTKEY_KEYBINDING.getKey().getName();
            smartGlassesAccess.getComputer().queueEvent("glassesKeyPressed", new Object[]{keyBind, keyPressDuration});
            keyPressDuration = 0;
        }
    }
}
