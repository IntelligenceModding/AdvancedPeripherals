package de.srendi.advancedperipherals.common.addons.powah;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import owmii.powah.block.energycell.EnergyCellTile;
import owmii.powah.block.ender.EnderCellTile;
import owmii.powah.block.furnator.FurnatorTile;
import owmii.powah.block.magmator.MagmatorTile;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.solar.SolarTile;
import owmii.powah.block.thermo.ThermoTile;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ReactorIntegration::new, ReactorPartTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(EnergyCellIntegration::new, EnergyCellTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(EnderCellIntegration::new, EnderCellTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SolarPanelIntegration::new, SolarTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FurnatorIntegration::new, FurnatorTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MagmatorIntegration::new, MagmatorTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ThermoIntegration::new, ThermoTile.class);
    }
}
