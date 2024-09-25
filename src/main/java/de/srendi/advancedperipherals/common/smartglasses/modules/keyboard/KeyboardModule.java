package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class KeyboardModule implements IModule {

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("keyboard");
    }

    @Nullable
    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return null;
    }
}
