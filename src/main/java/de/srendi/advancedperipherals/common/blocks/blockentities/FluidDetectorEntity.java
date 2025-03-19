package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.FluidDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.FluidStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class FluidDetectorEntity extends BaseDetectorEntity<IFluidHandler, FluidStorageProxy, FluidDetectorPeripheral> {

    private static final FluidTank ZERO_STORAGE = new FluidTank(0);

    public FluidDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.FLUID_DETECTOR.get(), pos, state, ForgeCapabilities.FLUID_HANDLER);
    }

    @Override
    @NotNull
    protected FluidDetectorPeripheral createPeripheral() {
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
}
