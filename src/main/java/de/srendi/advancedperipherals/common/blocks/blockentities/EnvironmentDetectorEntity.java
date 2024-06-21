package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EnvironmentDetectorEntity extends PoweredPeripheralBlockEntity<EnvironmentDetectorPeripheral> {

    public EnvironmentDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ENVIRONMENT_DETECTOR.get(), pos, state);
    }

    @Override
    protected int getMaxEnergyStored() {
        return APConfig.PERIPHERALS_CONFIG.poweredPeripheralMaxEnergyStorage.get();
    }

    @NotNull
    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral(this);
    }

}
