package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import org.jetbrains.annotations.NotNull;

public class EnvironmentDetectorTile extends PoweredPeripheralTileEntity<EnvironmentDetectorPeripheral> {

    public EnvironmentDetectorTile() {
        super(TileEntityTypes.ENVIRONMENT_DETECTOR.get());
    }

    @Override
    protected int getMaxEnergyStored() {
        return AdvancedPeripheralsConfig.poweredPeripheralMaxEnergyStored;
    }

    @NotNull
    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral("environmentDetector", this);
    }


}
