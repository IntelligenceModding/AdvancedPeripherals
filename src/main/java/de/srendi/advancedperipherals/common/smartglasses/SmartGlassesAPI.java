package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.ILuaAPI;

public class SmartGlassesAPI implements ILuaAPI {
    @Override
    public String[] getNames() {
        return new String[]{"smartglasses"};
    }

}
