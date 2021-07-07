package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class FuelConsumingPeripheral extends BasePeripheral {
    public FuelConsumingPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public FuelConsumingPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public FuelConsumingPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    public FuelConsumingPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    public int getFuelCount() {
        if (turtle != null)
            return turtle.getFuelLevel();
        if (tileEntity != null)
            return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
        return 0;
    }

    public int getMaxFuelCount() {
        if (turtle != null)
            return turtle.getFuelLimit();
        if (tileEntity != null)
            return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getMaxEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
        return 0;
    }

    public boolean consumeFuel(@Nonnull IComputerAccess access, int count) {
        return consumeFuel(access, count, false);
    }

    public boolean consumeFuel(@Nonnull IComputerAccess access, int count, boolean simulate) {
        if (turtle != null) {
            if (turtle.getFuelLevel() >= count) {
                if (simulate) return true;
                return turtle.consumeFuel(count);
            }
        }
        if (tileEntity != null) {
            return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> {
                int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
                int extractedCount = storage.extractEnergy(energyCount, true);
                if (extractedCount == energyCount) {
                    if (simulate)
                        return true;
                    storage.extractEnergy(energyCount, false);
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    public Optional<MethodResult> consumeFuelOp(@Nonnull IComputerAccess access, int count) {
        if (!consumeFuel(access, count))
            return Optional.of(MethodResult.of(null, String.format("Not enough fuel, %d needed", count)));
        return Optional.empty();
    }

    public void addFuel(int count) {
        if (turtle != null)
            turtle.addFuel(count);
        if (tileEntity != null)
            tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage -> {
                int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
                storage.receiveEnergy(energyCount, false);
            });
    }

    @LuaFunction(mainThread = true)
    public int getFuelLevel() {
        return getFuelCount();
    }

    @LuaFunction(mainThread = true)
    public int getMaxFuelLevel() {
        return getMaxFuelCount();
    }
}
