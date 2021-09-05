package de.srendi.advancedperipherals.common.addons.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorRedstoneTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(RedstoneConnectorIntegration::new, ConnectorRedstoneTileEntity.class);
        IntegrationPeripheralProvider.registerTileEntityIntegration(RedstoneProbeIntegration::new, ConnectorProbeTileEntity.class, 25);
    }
}
