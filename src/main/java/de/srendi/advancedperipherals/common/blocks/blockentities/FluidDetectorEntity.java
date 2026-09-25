package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.FluidDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.FluidStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public class FluidDetectorEntity extends BaseDetectorEntity<IFluidHandler, FluidStorageProxy, FluidDetectorPeripheral> {

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

    public static class Type<T extends FluidDetectorEntity> extends BaseDetectorEntity.Type<T, IFluidHandler> {
        public Type(BlockEntitySupplier<? extends T> factory, Set<Block> validBlocks, com.mojang.datafixers.types.Type<?> dataType) {
            super(factory, validBlocks, dataType, Capabilities.FluidHandler.BLOCK);
        }
    }
}
