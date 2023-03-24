package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;

public class OverlayGlassesItem extends BaseItem implements IModuleItem {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IModule getModule() {
        return new OverlayModule();
    }
}
