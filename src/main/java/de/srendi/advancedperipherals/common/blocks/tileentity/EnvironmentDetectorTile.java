package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import org.jetbrains.annotations.NotNull;

public class EnvironmentDetectorTile extends PoweredPeripheralTileEntity<EnvironmentDetectorPeripheral> {

    public EnvironmentDetectorTile() {
        super(TileEntityTypes.ENVIRONMENT_DETECTOR.get());
    }

    @Override
    protected int getMaxEnergyStored() {
        return APConfig.PERIPHERALS_CONFIG.POWERED_PERIPHERAL_MAX_ENERGY_STORAGE.get();
    }

    @NotNull
    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral(this);
    }


}
