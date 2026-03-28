package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;

public class HotkeyModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("hotkey");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesSideAccess smartGlassesAccess) {
        return null;
    }

}
