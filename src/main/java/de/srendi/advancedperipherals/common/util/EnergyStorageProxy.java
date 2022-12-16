package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Optional;

public class EnergyStorageProxy implements IEnergyStorage {

    private final EnergyDetectorEntity energyDetectorTE;
    private int maxTransferRate;
    private int transferedInThisTick = 0;

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
        Optional<IEnergyStorage> out = energyDetectorTE.getOutputStorage();
        return out.map(outStorage -> {
            int transferred = outStorage.receiveEnergy(Math.min(maxReceive, maxTransferRate), simulate);
            if (!simulate) {
                transferedInThisTick += transferred;
            }
            return transferred;
        }).orElse(0);
    }

    @Override
    public int getEnergyStored() {
        Optional<IEnergyStorage> out = energyDetectorTE.getOutputStorage();
        return out.map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        Optional<IEnergyStorage> out = energyDetectorTE.getOutputStorage();
        return out.map(IEnergyStorage::getMaxEnergyStored).orElse(0);
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
        transferedInThisTick = 0;
    }

    public int getTransferedInThisTick() {
        return transferedInThisTick;
    }
}
