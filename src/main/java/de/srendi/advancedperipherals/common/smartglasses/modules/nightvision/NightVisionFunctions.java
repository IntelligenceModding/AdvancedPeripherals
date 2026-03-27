package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;

public class NightVisionFunctions implements IModuleFunctions {

    private final NightVisionModule nightVisionModule;

    public NightVisionFunctions(NightVisionModule nightVisionModule) {
        this.nightVisionModule = nightVisionModule;
    }

    @LuaFunction
    public final boolean isNightVisionEnabled() {
        return nightVisionModule.isNightVisionEnabled();
    }

    @LuaFunction
    public final void enableNightVision(boolean enable) {
        nightVisionModule.enableNightVision(enable);
    }

}
