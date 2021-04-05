package de.srendi.advancedperipherals.common.util;

import net.minecraftforge.energy.EnergyStorage;

public class APEnergyStorage extends EnergyStorage {

    public APEnergyStorage(int capacity) {
        super(capacity);
    }

    public APEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public APEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public APEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public int extractPower(int energy, boolean simulate, boolean ignoreMaxTransfer) {
        if (!canExtract() && !ignoreMaxTransfer)
            return 0;

        int energyExtracted = ignoreMaxTransfer ? Math.min(energy, this.capacity) : Math.min(this.energy, Math.min(this.maxExtract, energy));
        if (!simulate)
            this.energy -= energyExtracted;
        return energyExtracted;
    }

    public int receivePower(int energy, boolean simulate, boolean ignoreMaxTransfer) {
        if (!canReceive() && !ignoreMaxTransfer)
            return 0;

        int energyReceived = ignoreMaxTransfer ? Math.min(energy, this.capacity) : Math.min(this.capacity - this.energy, Math.min(this.maxReceive, energy));
        if (!simulate)
            this.energy += energyReceived;
        return energyReceived;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, capacity);
    }

    public void setMaxReceive(int receive) {
        this.maxReceive = receive;
    }

    public void setMaxExtract(int extract) {
        this.maxExtract = extract;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
