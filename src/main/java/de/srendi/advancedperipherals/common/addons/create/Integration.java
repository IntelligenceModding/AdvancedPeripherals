package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.relays.advanced.SpeedControllerTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(SpeedControllerIntegration::new, SpeedControllerTileEntity.class);
    }
}
