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
    private boolean receiving = false;

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
        IFluidHandler storage = fluidDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.getFluidInTank(tank) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        IFluidHandler storage = fluidDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.getTankCapacity(tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        IFluidHandler storage = fluidDetectorEntity.getOutputStorage().orElse(null);
        return storage != null ? storage.isFluidValid(tank, stack) : false;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.receiving) {
            return 0;
        }
        this.receiving = true;
        try {
            IFluidHandler storage = fluidDetectorEntity.getOutputStorage().orElse(null);
            if (storage == null) {
                return 0;
            }
            FluidStack transferring = resource.copy();
            transferring.setAmount((int) Math.min(resource.getAmount(), this.getTransferRate()));
            int transferred = storage.fill(transferring, action);
            ResourceLocation id = ForgeRegistries.FLUIDS.getKey(resource.getFluid());
            // TODO: what if filler may transfer multiple types of fluids?
            this.wasReady = id;
            if (!action.simulate() && transferred > 0) {
                this.onTransfered(transferred);
                this.lastTransfered = id;
            }
            return transferred;
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
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }
}
