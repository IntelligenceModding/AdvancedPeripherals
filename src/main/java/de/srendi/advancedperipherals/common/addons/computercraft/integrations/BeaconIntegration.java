package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeaconIntegration extends BlockEntityIntegrationPeripheral<BeaconBlockEntity> {

    public BeaconIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    public @NotNull String getType() {
        return "beacon";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction(mainThread = true)
    public final int getLevel() {
        // because levels are now protected field .... why?
        CompoundTag savedData = blockEntity.saveWithoutMetadata();
        return savedData.getInt("Levels");
    }

    @LuaFunction(mainThread = true)
    public final String getPrimaryEffect() {
        return blockEntity.primaryPower == null ? "none" : blockEntity.primaryPower.getDescriptionId();
    }

    @LuaFunction(mainThread = true)
    public final String getSecondaryEffect() {
        return blockEntity.secondaryPower == null ? "none" : blockEntity.secondaryPower.getDescriptionId();
    }

}
