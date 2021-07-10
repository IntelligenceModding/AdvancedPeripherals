package de.srendi.advancedperipherals.api.peripheral;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public interface IPeripheralTileEntity {
    CompoundNBT getApSettings();
}
