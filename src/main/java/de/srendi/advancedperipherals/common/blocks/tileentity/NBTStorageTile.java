package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.NBTStoragePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NBTStorageTile extends PeripheralTileEntity<NBTStoragePeripheral> {

    private CompoundTag stored;

    public NBTStorageTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.NBT_STORAGE.get(), pos, state);
        stored = new CompoundTag();
    }

    @NotNull
    @Override
    protected NBTStoragePeripheral createPeripheral() {
        return new NBTStoragePeripheral("nbtStorage", this);
    }

    public CompoundTag getStored() {
        return stored;
    }

    public void setStored(CompoundTag newStored) {
        stored = newStored;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound = super.save(compound);
        compound.put("storedData", stored);
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        stored = compound.getCompound("storedData");
        super.load(compound);
    }

}
