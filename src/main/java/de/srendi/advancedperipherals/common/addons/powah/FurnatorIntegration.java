package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.item.ItemStack;
import owmii.powah.block.furnator.FurnatorTile;

public class FurnatorIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "furnator";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Furnator";
    }

    @LuaFunction(mainThread = true)
    public final boolean isBurning(FurnatorTile blockEntity) {
        return blockEntity.isBurning();
    }

    // TODO: remove in the next major version
    @Deprecated(forRemoval = true, since = "1.20.1-0.7.41r")
    @LuaFunction(mainThread = true)
    public final double getEnergy(FurnatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(FurnatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(FurnatorTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon(FurnatorTile blockEntity) {
        return blockEntity.getCarbon().perCent();
    }

    @LuaFunction(mainThread = true)
    public final Object getInventory(FurnatorTile blockEntity) {
        ItemStack stack = blockEntity.getInventory().getStackInSlot(1);
        if (stack.isEmpty()) {
            return null;
        }
        return LuaConverter.stackToObject(stack);
    }
}
