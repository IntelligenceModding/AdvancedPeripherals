package de.srendi.advancedperipherals.common.util.proxy;

import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GasStorageProxy extends AbstractStorageProxy implements IChemicalHandler {

    private final GasDetectorEntity gasDetectorEntity;
    private ResourceLocation lastTransfered = null;
    private ResourceLocation wasReady = null;
    private volatile ResourceLocation ready = null;
    private boolean receiving = false;

    public GasStorageProxy(GasDetectorEntity gasDetectorEntity, int maxTransferRate) {
        super(maxTransferRate);
        this.gasDetectorEntity = gasDetectorEntity;
    }

    @Override
    public int getChemicalTanks() {
        return 1;
    }

    @NotNull
    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        IChemicalHandler storage = gasDetectorEntity.getOutputStorage();
        return storage != null ? storage.getChemicalInTank(tank) : ChemicalStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull ChemicalStack stack) {
        IChemicalHandler storage = gasDetectorEntity.getOutputStorage();
        if (storage != null) {
            storage.setChemicalInTank(tank, stack);
        }
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        IChemicalHandler storage = gasDetectorEntity.getOutputStorage();
        return storage != null ? storage.getChemicalTankCapacity(tank) : 0L;
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        IChemicalHandler storage = gasDetectorEntity.getOutputStorage();
        return storage != null ? storage.isValid(tank, stack) : false;
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(@NotNull ChemicalStack stack, @NotNull Action action) {
        if (this.receiving) {
            return ChemicalStack.EMPTY;
        }
        this.receiving = true;
        try {
            IChemicalHandler storage = gasDetectorEntity.getOutputStorage();
            if (storage == null) {
                return ChemicalStack.EMPTY;
            }
            ChemicalStack transferring = stack.copyWithAmount(Math.min(stack.getAmount(), this.getTransferRate()));
            ChemicalStack left = storage.insertChemical(transferring, action);
            if (!action.simulate()) {
                this.wasReady = stack.getTypeRegistryName();
                long transferred = transferring.getAmount() - left.getAmount();
                if (transferred > 0) {
                    this.onTransfered(transferred);
                    this.lastTransfered = this.wasReady;
                }
            }
            return left;
        } finally {
            this.receiving = false;
        }
    }

    @Override
    public String getLastTransferedId() {
        return this.lastTransfered == null ? null : this.lastTransfered.toString();
    }

    @Override
    public String getReadyTransferId() {
        return this.ready == null ? null : this.ready.toString();
    }

    @Override
    protected void resetStatus() {
        super.resetStatus();
        this.ready = this.wasReady;
        this.wasReady = null;
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, @NotNull ChemicalStack stack, @NotNull Action action) {
        return insertChemical(stack, action);
    }

    @NotNull
    @Override
    public ChemicalStack extractChemical(int tank, long amount, @NotNull Action action) {
        return ChemicalStack.EMPTY;
    }
}
