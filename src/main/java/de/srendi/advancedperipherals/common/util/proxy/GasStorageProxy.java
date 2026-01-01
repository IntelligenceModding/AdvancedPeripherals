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
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getChemicalInTank(tank)).orElse(ChemicalStack.EMPTY);
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull ChemicalStack stack) {
        gasDetectorEntity.getOutputStorage().ifPresent(outStorage -> outStorage.setChemicalInTank(tank, stack));
    }

    @Override
    public long getTankCapacity(int tank) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getTankCapacity(tank)).orElse(0L);
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.isValid(tank, stack)).orElse(false);
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(@NotNull ChemicalStack stack, @NotNull Action action) {
        if (this.receiving) {
            return ChemicalStack.EMPTY;
        }
        this.receiving = true;
        try {
            IChemicalHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
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
