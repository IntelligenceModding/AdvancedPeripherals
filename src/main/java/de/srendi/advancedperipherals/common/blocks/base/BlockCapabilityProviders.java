package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class BlockCapabilityProviders {
    public interface EnergyStorage {
        @Nullable
        IEnergyStorage createEnergyStorageCap(@Nullable Direction side);
    }

    public interface FluidHandler {
        @Nullable
        IFluidHandler createFluidHandlerCap(@Nullable Direction side);
    }

    public interface ItemHandler {
        @Nullable
        IItemHandler createItemHandlerCap(@Nullable Direction side);
    }

    public interface Peripheral {
        @Nullable
        IPeripheral createPeripheralCap(@Nullable Direction side);
    }

    public interface ChemicalHandler {
        @Nullable
        Object /*IChemicalHandler*/ createChemicalHandlerCap(@Nullable Direction side);
    }
}
