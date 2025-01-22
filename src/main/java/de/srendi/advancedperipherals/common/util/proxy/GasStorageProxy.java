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
    public GasStack getChemicalInTank(int tank) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getChemicalInTank(tank)).orElse(GasStack.EMPTY);
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull GasStack stack) {
        gasDetectorEntity.getOutputStorage().ifPresent(outStorage -> outStorage.setChemicalInTank(tank, stack));
    }

    @Override
    public long getTankCapacity(int tank) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getTankCapacity(tank)).orElse(0L);
    }

    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> outStorage.isValid(tank, stack)).orElse(false);
    }

    @NotNull
    @Override
    public GasStack insertChemical(@NotNull GasStack stack, @NotNull Action action) {
        return gasDetectorEntity.getOutputStorage().map(outStorage -> {
            GasStack transferring = stack.copy();
            transferring.setAmount(Math.min(stack.getAmount(), this.getTransferRate()));
            GasStack left = outStorage.insertChemical(transferring, action);
            if (!action.simulate()) {
                this.wasReady = stack.getTypeRegistryName();
                long transferred = transferring.getAmount() - left.getAmount();
                if (transferred > 0) {
                    this.onTransfered(transferred);
                    this.lastTransfered = this.wasReady;
                }
            }
            return left;
        }).orElse(GasStack.EMPTY);
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
    public GasStack insertChemical(int tank, @NotNull GasStack stack, @NotNull Action action) {
        return insertChemical(stack, action);
    }

    @NotNull
    @Override
    public GasStack extractChemical(int tank, long amount, @NotNull Action action) {
        return GasStack.EMPTY;
    }
}
