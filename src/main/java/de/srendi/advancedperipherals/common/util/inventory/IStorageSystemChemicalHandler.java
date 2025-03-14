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
     * @param count The amount to extract
     * @param simulate Should this action be simulated
     * @return extracted from the slot, must be empty if nothing can be extracted. The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     */
    ChemicalStack extractChemical(ChemicalFilter filter, long count, Action simulate);

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
        return ChemicalStack.EMPTY;
    }

    @Override
    default ChemicalStack extractChemical(long amount, Action action) {
        return ChemicalStack.EMPTY;
    }

    @Override
    default ChemicalStack extractChemical(ChemicalStack stack, Action action) {
        return ChemicalStack.EMPTY;
    }

    @Override
    default ChemicalStack extractChemical(int tank, long amount, Action action) {
        return ChemicalStack.EMPTY;
    }

    @Override
    default void setChemicalInTank(int tank, ChemicalStack stack) {

    }

    @Override
    default boolean isValid(int tank, ChemicalStack stack) {
        return true;
    }
}
