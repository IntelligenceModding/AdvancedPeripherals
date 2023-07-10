package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import net.minecraft.resources.ResourceLocation;

public interface IModule {

    ResourceLocation getName();

    /**
     * Used to define the available functions of the module. This method only gets called once when indexing the modules
     *
     * @return an object containing lua functions {@link dan200.computercraft.api.lua.LuaFunction}
     */
    IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess);

    void tick(SmartGlassesAccess smartGlassesAccess);

}
