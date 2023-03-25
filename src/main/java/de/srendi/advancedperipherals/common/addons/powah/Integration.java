package de.srendi.advancedperipherals.common.addons.powah;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import owmii.powah.block.reactor.ReactorTile;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ReactorIntegration::new, ReactorTile.class);
    }
}
