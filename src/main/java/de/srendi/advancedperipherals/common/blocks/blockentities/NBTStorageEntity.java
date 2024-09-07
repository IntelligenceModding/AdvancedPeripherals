package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.NBTStoragePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NBTStorageEntity extends PeripheralBlockEntity<NBTStoragePeripheral> {

    private CompoundTag stored;

    public NBTStorageEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.NBT_STORAGE.get(), pos, state);
        stored = new CompoundTag();
    }

    @NotNull
    @Override
    protected NBTStoragePeripheral createPeripheral() {
        return new NBTStoragePeripheral(this);
    }

    public CompoundTag getStored() {
        return stored;
    }

    public void setStored(CompoundTag newStored) {
        stored = newStored;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("storedData", stored);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        stored = compound.getCompound("storedData");
        super.load(compound);
    }

}
