package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.nbt.CompoundNBT;

public interface IPeripheralTileEntity {
    CompoundNBT getPeripheralSettings();

    void markSettingsChanged();
}
