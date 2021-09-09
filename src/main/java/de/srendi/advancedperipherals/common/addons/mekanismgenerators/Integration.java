package de.srendi.advancedperipherals.common.addons.mekanismgenerators;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(FusionIntegration::new, TileEntityFusionReactorLogicAdapter.class);
        IntegrationPeripheralProvider.registerTileEntityIntegration(FissionIntegration::new, TileEntityFissionReactorLogicAdapter.class);
        IntegrationPeripheralProvider.registerTileEntityIntegration(TurbineIntegration::new, TileEntityTurbineValve.class);
    }
}
