package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import org.jetbrains.annotations.NotNull;

public class NightVisionModuleItem extends BaseItem implements IModuleItem<NightVisionModule> {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @NotNull
    public NightVisionModule createModule(SmartGlassesSideAccess access) {
        return new NightVisionModule();
    }
}
