package de.srendi.advancedperipherals.api.peripherals;

import net.minecraft.nbt.CompoundNBT;

public interface IPeripheralTileEntity {
    CompoundNBT getPeripheralSettings();
    void markSettingsChanged();
}
