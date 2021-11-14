package de.srendi.advancedperipherals.common.addons.draconicevolution;

import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(EnergyPylonIntegration::new, TileEnergyPylon.class);
        IntegrationPeripheralProvider.registerTileEntityIntegration(ReactorIntegration::new, TileReactorStabilizer.class);
    }
}
