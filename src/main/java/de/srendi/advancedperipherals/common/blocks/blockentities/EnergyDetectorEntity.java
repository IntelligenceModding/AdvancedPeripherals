package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.proxy.EnergyStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyDetectorEntity extends BaseDetectorEntity<IEnergyStorage, EnergyStorageProxy, EnergyDetectorPeripheral> implements BlockCapabilityProviders.EnergyStorage {

    private static final EnergyStorage ZERO_STORAGE = new EnergyStorage(0, 0, 0);

    public EnergyDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ENERGY_DETECTOR.get(), pos, state, Capabilities.EnergyStorage.BLOCK);
    }

    @Override
    @NotNull
    protected EnergyDetectorPeripheral buildPeripheral() {
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

    @Override
    @Nullable
    public IEnergyStorage createEnergyStorageCap(@Nullable Direction side) {
        return this.getCapability(side);
    }
}
