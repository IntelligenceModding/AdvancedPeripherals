package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GasDetectorPeripheral;
import de.srendi.advancedperipherals.common.addons.mekanism.MekanismCapabilities;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.GasStorageProxy;
import de.srendi.advancedperipherals.common.util.proxy.ZeroGasTank;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GasDetectorEntity extends BaseDetectorEntity<IGasHandler, GasStorageProxy, GasDetectorPeripheral> {

    // a zero size, zero transfer gas storage to ensure that cables connect
    private static final IGasHandler ZERO_STORAGE = new ZeroGasTank();

    public GasDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.GAS_DETECTOR.get(), pos, state, MekanismCapabilities.GAS_HANDLER);
    }

    @Override
    @NotNull
    protected GasDetectorPeripheral createPeripheral() {
        return new GasDetectorPeripheral(this);
    }

    @Override
    @NotNull
    protected GasStorageProxy createProxy() {
        return new GasStorageProxy(this, APConfig.PERIPHERALS_CONFIG.gasDetectorMaxFlow.get());
    }

    @Override
    @NotNull
    protected IGasHandler getZeroStorage() {
        return ZERO_STORAGE;
    }
}
