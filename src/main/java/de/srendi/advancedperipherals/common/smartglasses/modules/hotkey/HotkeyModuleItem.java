package de.srendi.advancedperipherals.common.smartglasses.modules.hotkey;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;

public class HotkeyModuleItem extends BaseItem implements IModuleItem {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IModule getModule(SmartGlassesAccess access) {
        return new HotkeyModule();
    }
}
