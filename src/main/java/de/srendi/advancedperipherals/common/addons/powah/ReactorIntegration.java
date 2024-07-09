package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.item.ItemStack;
import owmii.powah.block.reactor.ReactorPartTile;

public class ReactorIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "uraniniteReactor";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return false;
        return blockEntity.core().get().isRunning();
    }

    @LuaFunction(mainThread = true)
    public final double getFuel(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().fuel.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().carbon.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getRedstone(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().redstone.perCent();
    }

    // TODO: remove in the next major version
    @Deprecated(forRemoval = true, since = "1.20.1-0.7.41r")
    @LuaFunction(mainThread = true)
    public final double getEnergy(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().temp.perCent();
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryUraninite(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(1);
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryRedstone(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(3);
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryCarbon(ReactorPartTile blockEntity) {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(2);
    }
}
