package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface IModuleItem<T extends IModule> {

    @NotNull
    T createModule(SmartGlassesSideAccess access);

    /**
     * This method is called every tick the item is in the inventory of the smart glasses.
     * Runs on both client and server side.
     *
     * @param access The access to the smart glasses - Null on client side
     * @param module The module - Null on client side
     */
    default void moduleTick(Level level, LivingEntity entity, int moduleSlot, SmartGlassesSideAccess access, T module) {}
}
