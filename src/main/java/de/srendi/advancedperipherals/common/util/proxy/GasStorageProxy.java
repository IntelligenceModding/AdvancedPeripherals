package de.srendi.advancedperipherals.common.util.proxy;

import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GasStorageProxy extends AbstractStorageProxy implements IGasHandler {

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

    @Override
    @NotNull
    public GasStack getChemicalInTank(int tank) {
        IGasHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.getChemicalInTank(tank) : GasStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull GasStack stack) {
        IGasHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
        if (storage != null) {
            storage.setChemicalInTank(tank, stack);
        }
    }

    @Override
    public long getTankCapacity(int tank) {
        IGasHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.getTankCapacity(tank) : 0L;
    }

    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        IGasHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.isValid(tank, stack) : false;
    }

    @Override
    @NotNull
    public GasStack insertChemical(@NotNull GasStack stack, @NotNull Action action) {
        if (this.receiving) {
            return GasStack.EMPTY;
        }
        this.receiving = true;
        try {
            IGasHandler storage = gasDetectorEntity.getOutputStorage().orElse(null);
            if (storage == null) {
                return GasStack.EMPTY;
            }
            GasStack transferring = stack.copy();
            transferring.setAmount(Math.min(stack.getAmount(), this.getTransferRate()));
            GasStack left = storage.insertChemical(transferring, action);
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

    @Override
    @NotNull
    public GasStack insertChemical(int tank, @NotNull GasStack stack, @NotNull Action action) {
        return insertChemical(stack, action);
    }

    @Override
    @NotNull
    public GasStack extractChemical(int tank, long amount, @NotNull Action action) {
        return GasStack.EMPTY;
    }
}
