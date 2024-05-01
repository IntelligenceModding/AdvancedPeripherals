package de.srendi.advancedperipherals.common.util;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.NotNull;

public class ZeroGasTank implements IGasHandler {

    @Override
    public int getTanks() {
        return 0;
    }

    @NotNull
    @Override
    public GasStack getChemicalInTank(int tank) {
        return GasStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, GasStack stack) {

    }

    @Override
    public long getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        return false;
    }

    @NotNull
    @Override
    public GasStack insertChemical(int tank, @NotNull GasStack stack, @NotNull Action action) {
        return GasStack.EMPTY;
    }

    @NotNull
    @Override
    public GasStack extractChemical(int tank, long amount, @NotNull Action action) {
        return GasStack.EMPTY;
    }
}
