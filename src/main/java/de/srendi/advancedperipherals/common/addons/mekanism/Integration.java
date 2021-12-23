package de.srendi.advancedperipherals.common.addons.mekanism;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.tile.multiblock.TileEntityBoilerValve;
import mekanism.common.tile.multiblock.TileEntityDynamicValve;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationValve;
import mekanism.common.tile.transmitter.*;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(LogisticalTransporterIntegration::new, TileEntityLogisticalTransporter.class);
        IntegrationPeripheralProvider.registerTileEntityIntegration(ThermodynamicConductorIntegration::new, TileEntityThermodynamicConductor.class);

        EnvironmentDetectorPeripheral.addIntegrationPlugin(EnvironmentDetectorPlugin::new);
    }
}
