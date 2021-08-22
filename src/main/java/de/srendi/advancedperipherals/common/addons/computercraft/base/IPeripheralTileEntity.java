package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IPeripheralTileEntity {
    CompoundTag getPeripheralSettings();
    default <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {

    }
}
