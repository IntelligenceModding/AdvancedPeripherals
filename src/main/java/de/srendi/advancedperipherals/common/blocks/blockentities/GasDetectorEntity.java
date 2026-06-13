package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GasDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.GasStorageProxy;
import de.srendi.advancedperipherals.common.util.proxy.ZeroGasTank;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GasDetectorEntity extends BaseDetectorEntity<IChemicalHandler, GasStorageProxy, GasDetectorPeripheral> implements BlockCapabilityProviders.ChemicalHandler {

    private static final IChemicalHandler ZERO_STORAGE = new ZeroGasTank();

    public GasDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.GAS_DETECTOR.get(), pos, state, Capabilities.CHEMICAL.block());
    }

    @Override
    @NotNull
    protected GasDetectorPeripheral buildPeripheral() {
        return new GasDetectorPeripheral(this);
    }

    @Override
    @NotNull
    public GasStorageProxy createProxy() {
        return new GasStorageProxy(this, APConfig.PERIPHERALS_CONFIG.gasDetectorMaxFlow.get());
    }

    @Override
    @NotNull
    protected IChemicalHandler getZeroStorage() {
        return ZERO_STORAGE;
    }

    @Override
    @Nullable
    public Object createChemicalHandlerCap(@Nullable Direction side) {
        return this.getCapability(side);
    }
}
