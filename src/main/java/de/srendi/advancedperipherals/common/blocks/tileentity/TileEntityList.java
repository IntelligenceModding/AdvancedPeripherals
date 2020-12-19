package de.srendi.advancedperipherals.common.blocks.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class TileEntityList extends WorldSavedData {


    public TileEntityList(String name) {
        super(name);
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return null;
    }
}