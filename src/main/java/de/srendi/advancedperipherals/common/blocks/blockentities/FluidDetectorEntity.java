package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.FluidDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.FluidStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidDetectorEntity extends BaseDetectorEntity<IFluidHandler, FluidStorageProxy, FluidDetectorPeripheral> implements BlockCapabilityProviders.FluidHandler {

    private static final FluidTank ZERO_STORAGE = new FluidTank(0);

    public FluidDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.FLUID_DETECTOR.get(), pos, state, Capabilities.FluidHandler.BLOCK);
    }

    @Override
    @NotNull
    protected FluidDetectorPeripheral buildPeripheral() {
        return new FluidDetectorPeripheral(this);
    }

    @Override
    @NotNull
    protected FluidStorageProxy createProxy() {
        return new FluidStorageProxy(this, APConfig.PERIPHERALS_CONFIG.fluidDetectorMaxFlow.get());
    }

    @Override
    @NotNull
    protected IFluidHandler getZeroStorage() {
        return ZERO_STORAGE;
    }

    @Override
    @Nullable
    public IFluidHandler createFluidHandlerCap(@Nullable Direction side) {
        return this.getCapability(side);
    }
}
