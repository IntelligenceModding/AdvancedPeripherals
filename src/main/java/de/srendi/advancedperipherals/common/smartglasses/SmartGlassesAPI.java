package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.ILuaAPI;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;

public class SmartGlassesAPI implements ILuaAPI {
    @Override
    public String[] getNames() {
        return new String[]{"smartglasses"};
    }

    public static ILuaAPI create(IComputerSystem system) {
        final Boolean isSmartGlasses = system.getComponent(APComputerComponents.SMARTGLASSES);
        if (isSmartGlasses != Boolean.TRUE) {
            return null;
        }
        return new SmartGlassesAPI();
    }
}
