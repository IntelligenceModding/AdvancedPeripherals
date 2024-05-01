/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.blocks.blockentities.FluidDetectorEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FluidStorageProxy implements IFluidHandler {

    private final FluidDetectorEntity fluidDetectorEntity;
    private int maxTransferRate;
    private int transferedInThisTick = 0;
    private final Fluid fluid = Fluids.EMPTY;

    public FluidStorageProxy(FluidDetectorEntity fluidDetectorEntity, int maxTransferRate) {
        this.fluidDetectorEntity = fluidDetectorEntity;
        this.maxTransferRate = maxTransferRate;
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

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        Optional<IFluidHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.getFluidInTank(tank)).orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        Optional<IFluidHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.getTankCapacity(tank)).orElse(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        Optional<IFluidHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> outStorage.isFluidValid(tank, stack)).orElse(false);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        Optional<IFluidHandler> out = fluidDetectorEntity.getOutputStorage();
        return out.map(outStorage -> {
            FluidStack transferring = resource.copy();
            transferring.setAmount(Math.min(resource.getAmount(), maxTransferRate));
            int transferred = outStorage.fill(transferring, action);
            if (!action.simulate()) {
                transferedInThisTick += transferred;
                fluidDetectorEntity.lastFlowedLiquid = resource.copy();
                // transferedInThisTick = transferred;
            }
            return transferred;
        }).orElse(0);
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
