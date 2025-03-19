package de.srendi.advancedperipherals.common.util.proxy;

import de.srendi.advancedperipherals.common.blocks.blockentities.FluidDetectorEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class FluidStorageProxy extends AbstractStorageProxy implements IFluidHandler {

    private final FluidDetectorEntity fluidDetectorEntity;
    private ResourceLocation lastTransfered = null;
    private ResourceLocation wasReady = null;
    private volatile ResourceLocation ready = null;

    public FluidStorageProxy(FluidDetectorEntity fluidDetectorEntity, int maxTransferRate) {
        super(maxTransferRate);
        this.fluidDetectorEntity = fluidDetectorEntity;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getFluidInTank(tank)).orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidDetectorEntity.getOutputStorage().map(outStorage -> outStorage.getTankCapacity(tank)).orElse(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluidDetectorEntity.getOutputStorage().map(outStorage -> outStorage.isFluidValid(tank, stack)).orElse(false);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return fluidDetectorEntity.getOutputStorage().map(outStorage -> {
            FluidStack transferring = resource.copy();
            transferring.setAmount((int) Math.min(resource.getAmount(), this.getTransferRate()));
            int transferred = outStorage.fill(transferring, action);
            if (!action.simulate()) {
                this.wasReady = ForgeRegistries.FLUIDS.getKey(resource.getFluid());
                if (transferred > 0) {
                    this.onTransfered(transferred);
                    this.lastTransfered = this.wasReady;
                }
            }
            return transferred;
        }).orElse(0);
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
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }
}
