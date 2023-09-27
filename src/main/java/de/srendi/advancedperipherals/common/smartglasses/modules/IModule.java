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

    /**
     * Classic tick function.
     * <p>
     * Implementations should check if the entity is not null since the glasses can still tick without belonging to an entity
     * @param smartGlassesAccess Contains access to the entity, the computer, the level or the upgrades
     */
    void tick(SmartGlassesAccess smartGlassesAccess);

    /**
     * ErrorConstants class contains constants for error messages. This is used for easier error handling for users.
     */
    class ErrorConstants {
        public static final String ALREADY_EXISTS = "ID_ALREADY_EXISTS";
    }

}
