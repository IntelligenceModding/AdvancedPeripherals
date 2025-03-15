package de.srendi.advancedperipherals.common.util.proxy;

import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageProxy extends AbstractStorageProxy implements IEnergyStorage {

    private final EnergyDetectorEntity energyDetectorTE;
    private boolean lastTransfered = false;
    private boolean wasReady = false;
    private volatile boolean ready = false;

    public EnergyStorageProxy(EnergyDetectorEntity energyDetectorTE, int maxTransferRate) {
        super(maxTransferRate);
        this.energyDetectorTE = energyDetectorTE;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyDetectorTE.getOutputStorage().map(outStorage -> {
            int transferred = outStorage.receiveEnergy((int) Math.min(maxReceive, this.getTransferRate()), simulate);
            if (!simulate) {
                this.wasReady = true;
                if (transferred > 0) {
                    this.lastTransfered = true;
                    this.onTransfered(transferred);
                }
            }
            return transferred;
        }).orElse(0);
    }

    @Override
    public String getLastTransferedId() {
        return this.lastTransfered ? "forge:energy" : null;
    }

    @Override
    public String getReadyTransferId() {
        return this.ready ? "forge:energy" : null;
    }

    @Override
    protected void resetStatus() {
        super.resetStatus();
        this.ready = this.wasReady;
        this.wasReady = false;
    }

    @Override
    public int getEnergyStored() {
        return energyDetectorTE.getOutputStorage().map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        return energyDetectorTE.getOutputStorage().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }
}
