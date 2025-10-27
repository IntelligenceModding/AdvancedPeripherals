package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider {

    @Nullable
    default IPeripheral createPeripheralCap(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default IFluidHandler createFluidHandlerCap(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default IItemHandler createItemHandlerCap(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default IEnergyStorage createEnergyStorageCap(@Nullable Direction side) {
        return null;
    }

}
