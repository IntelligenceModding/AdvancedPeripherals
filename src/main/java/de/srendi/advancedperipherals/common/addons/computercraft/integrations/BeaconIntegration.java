package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconIntegration implements APGenericPeripheral {
    @Override
    public String getPeripheralType() {
        return "beacon";
    }

    @LuaFunction(mainThread = true)
    public final int getLevel(BeaconBlockEntity blockEntity) {
        return blockEntity.levels;
    }

    @LuaFunction(mainThread = true)
    public final String getPrimaryEffect(BeaconBlockEntity blockEntity) {
        return blockEntity.primaryPower == null ? null : BuiltInRegistries.MOB_EFFECT.getKey(blockEntity.primaryPower).toString();
    }

    @LuaFunction(mainThread = true)
    public final String getSecondaryEffect(BeaconBlockEntity blockEntity) {
        return blockEntity.secondaryPower == null ? null : BuiltInRegistries.MOB_EFFECT.getKey(blockEntity.secondaryPower).toString();
    }
}
