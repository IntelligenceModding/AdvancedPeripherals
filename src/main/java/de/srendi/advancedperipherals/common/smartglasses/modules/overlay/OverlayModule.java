package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;


/**
 * We want to support scripts which were made for the plethora classes. So we call this item the same as the overlay item from plethora
 * We'll also add the same functions
 */
public class OverlayModule implements IModule {

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "glasses");
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return IModuleFunctions.EMPTY;
    }

    @Override
    public void tick(SmartGlassesAccess smartGlassesAccess) {
        Entity entity = smartGlassesAccess.getEntity();
        if(entity != null && entity.getLevel().getGameTime() % 20 == 0)
            AdvancedPeripherals.LOGGER.info("I'm an overlay module! And I'm alive!");
    }
}
