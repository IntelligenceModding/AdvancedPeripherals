package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;

public class HotkeyModule implements IModule {

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("hotkey");
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return null;
    }

}
