package de.srendi.advancedperipherals.common.addons.powah;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import owmii.powah.block.energycell.EnergyCellTile;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.solar.SolarTile;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ReactorIntegration::new, ReactorPartTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(EnergyCellIntegration::new, EnergyCellTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SolarPanelIntegration::new, SolarTile.class);
    }
}
