package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.EnergyStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class EnergyDetectorEntity extends BaseDetectorEntity<IEnergyStorage, EnergyStorageProxy, EnergyDetectorPeripheral> {

    private static final EnergyStorage ZERO_STORAGE = new EnergyStorage(0, 0, 0);

    public EnergyDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ENERGY_DETECTOR.get(), pos, state, ForgeCapabilities.ENERGY);
    }

    @Override
    @NotNull
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral(this);
    }

    @Override
    @NotNull
    protected EnergyStorageProxy createProxy() {
        return new EnergyStorageProxy(this, APConfig.PERIPHERALS_CONFIG.energyDetectorMaxFlow.get());
    }

    @Override
    @NotNull
    protected IEnergyStorage getZeroStorage() {
        return ZERO_STORAGE;
    }
}
