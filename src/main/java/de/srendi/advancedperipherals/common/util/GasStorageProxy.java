package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GasStorageProxy implements IGasHandler {

    private final GasDetectorEntity fluidDetectorEntity;
    private int maxTransferRate;
    private int transferedInThisTick = 0;

    public GasStorageProxy(GasDetectorEntity fluidDetectorEntity, int maxTransferRate) {
        this.fluidDetectorEntity = fluidDetectorEntity;
        this.maxTransferRate = maxTransferRate;
    }


    public int getMaxTransferRate() {
        return maxTransferRate;
    }

    public void setMaxTransferRate(int rate) {
        maxTransferRate = rate;
    }

    /**
     * should be called on every tick
     */
    public void resetTransferedInThisTick() {
        transferedInThisTick = 0;
    }

    public int getTransferedInThisTick() {
        return transferedInThisTick;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public GasStack getChemicalInTank(int tank) {
        Optional<IGasHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.getChemicalInTank(tank)).orElse(GasStack.EMPTY);
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull GasStack stack) {
        Optional<IGasHandler> out = fluidDetectorEntity.getOutputStorage();
        out.ifPresent(outStorage -> outStorage.setChemicalInTank(tank, stack));
    }

    @Override
    public long getTankCapacity(int tank) {
        Optional<IGasHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.getTankCapacity(tank)).orElse(0L);
    }

    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        Optional<IGasHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.isValid(tank, stack)).orElse(false);
    }

    @NotNull
    @Override
    public GasStack insertChemical(@NotNull GasStack stack, @NotNull Action action) {
        Optional<IGasHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> {
            GasStack transferring = stack.copy();
            transferring.setAmount(Math.min(stack.getAmount(), maxTransferRate));
            GasStack transferred = outStorage.insertChemical(transferring, action);
            if (!action.simulate()) {
                transferedInThisTick += transferring.getAmount() - transferred.getAmount();
                fluidDetectorEntity.lastFlowedGas = stack.copy();
            }
            return transferred;
        }).orElse(GasStack.EMPTY);
    }

    @NotNull
    @Override
    public GasStack insertChemical(int tank, @NotNull GasStack stack, @NotNull Action action) {
        return insertChemical(stack, action);
    }

    @NotNull
    @Override
    public GasStack extractChemical(int tank, long amount, @NotNull Action action) {
        return GasStack.EMPTY;
    }
}
