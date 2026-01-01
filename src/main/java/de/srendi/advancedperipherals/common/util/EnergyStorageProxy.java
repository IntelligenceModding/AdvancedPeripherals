package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageProxy implements IEnergyStorage {

    private final EnergyDetectorEntity energyDetectorTE;
    private int maxTransferRate;
    private int transferredInThisTick = 0;
    private boolean receiving = false;

    public EnergyStorageProxy(EnergyDetectorEntity energyDetectorTE, int maxTransferRate) {
        this.energyDetectorTE = energyDetectorTE;
        this.maxTransferRate = maxTransferRate;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.receiving) {
            return 0;
        }
        this.receiving = true;
        try {
            IEnergyStorage out = energyDetectorTE.getOutputStorage();
            if (out == null) {
                return 0;
            }
            int transferred = out.receiveEnergy(Math.min(maxReceive, maxTransferRate - transferredInThisTick), simulate);
            if (!simulate) {
                transferredInThisTick += transferred;
            }
            return transferred;
        } finally {
            this.receiving = false;
        }
    }

    @Override
    public int getEnergyStored() {
        IEnergyStorage out = energyDetectorTE.getOutputStorage();
        if (out == null)
            return 0;
        return out.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        IEnergyStorage out = energyDetectorTE.getOutputStorage();
        if (out == null)
            return 0;
        return out.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
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
        transferredInThisTick = 0;
    }

    public int getTransferredInThisTick() {
        return transferredInThisTick;
    }
}
