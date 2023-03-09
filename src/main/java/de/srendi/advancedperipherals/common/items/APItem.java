package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;

import java.util.function.Supplier;

public class APItem extends BaseItem {

    private final Supplier<Boolean> enabledSup;

    public APItem(Properties properties, Supplier<Boolean> enabledSup) {
        super(properties);
        this.enabledSup = enabledSup;
    }

    public APItem(Supplier<Boolean> enabledSup) {
        super();
        this.enabledSup = enabledSup;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }

}
