package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;

public class SmartGlassesAPI implements ILuaAPI {
    @Override
    public String[] getNames() {
        return new String[]{"smartglasses"};
    }

}
