package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.NBTStoragePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;

public class NBTStorageTile extends PeripheralTileEntity<NBTStoragePeripheral> {

    private CompoundNBT stored;

    public NBTStorageTile() {
        super(TileEntityTypes.NBT_STORAGE.get());
        stored = new CompoundNBT();
    }

    @NotNull
    @Override
    protected NBTStoragePeripheral createPeripheral() {
        return new NBTStoragePeripheral(this);
    }

    public CompoundNBT getStored() {
        return stored;
    }

    public void setStored(CompoundNBT newStored) {
        stored = newStored;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound = super.save(compound);
        compound.put("storedData", stored);
        return compound;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        stored = compound.getCompound("storedData");
        super.load(blockState, compound);
    }
}
