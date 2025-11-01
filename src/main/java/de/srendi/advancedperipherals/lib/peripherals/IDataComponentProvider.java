package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.core.component.DataComponentType;

public interface IDataComponentProvider<T> {

    DataComponentType<T> dataComponentType();

}
