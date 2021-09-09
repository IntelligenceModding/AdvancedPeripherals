package de.srendi.advancedperipherals.common.addons.integrateddynamics;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import org.cyclops.integrateddynamics.tileentity.TileVariablestore;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(VariableStoreIntegration::new, TileVariablestore.class);
    }
}
