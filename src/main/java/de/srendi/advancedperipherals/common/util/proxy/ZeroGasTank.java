package de.srendi.advancedperipherals.common.util.proxy;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import org.jetbrains.annotations.NotNull;

public class ZeroGasTank implements IChemicalHandler {

    @Override
    public int getChemicalTanks() {
        return 0;
    }

    @NotNull
    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        return ChemicalStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, ChemicalStack stack) {

    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        return false;
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, @NotNull ChemicalStack stack, @NotNull Action action) {
        return ChemicalStack.EMPTY;
    }

    @NotNull
    @Override
    public ChemicalStack extractChemical(int tank, long amount, @NotNull Action action) {
        return ChemicalStack.EMPTY;
    }
}
