package de.srendi.advancedperipherals.common.util.inventory;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

public interface IStorageSystemChemicalHandler extends IChemicalHandler {

    /**
     * Used to extract an item from the system via a peripheral.
     * Uses a filter to find the right item. The amount should never be greater than 64
     * stack sizes greater than 64.
     *
     * @param filter The parsed filter
     * @param processor The extractation processor
     * @param action Should this action be simulated
     * @return extracted chemical amount
     */
    long extractChemicals(ChemicalFilter filter, StorageProcessor.Large<ChemicalStack> processor, Action action);

    @Override
    default int getChemicalTanks() {
        return 1;
    }

    @Override
    default long getChemicalTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    default ChemicalStack getChemicalInTank(int tank) {
        throw new UnsupportedOperationException();
    }

    @Override
    ChemicalStack insertChemical(ChemicalStack resource, Action action);

    @Override
    default ChemicalStack insertChemical(int tank, ChemicalStack resource, Action action) {
        return this.insertChemical(resource, action);
    }

    @Override
    default ChemicalStack extractChemical(long amount, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ChemicalStack extractChemical(ChemicalStack stack, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ChemicalStack extractChemical(int tank, long amount, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setChemicalInTank(int tank, ChemicalStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isValid(int tank, ChemicalStack stack) {
        return true;
    }
}
