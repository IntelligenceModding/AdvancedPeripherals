package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OverlayGlassesItem extends BaseItem implements IModuleItem<OverlayModule> {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ResourceLocation moduleId() {
        return OverlayModule.ID;
    }

    @Override
    @NotNull
    public OverlayModule createModule(SmartGlassesSideAccess access) {
        return new OverlayModule(access);
    }
}
