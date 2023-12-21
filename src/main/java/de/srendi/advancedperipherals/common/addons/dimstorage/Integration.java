package de.srendi.advancedperipherals.common.addons.dimstorage;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import edivad.dimstorage.blockentities.BlockEntityDimChest;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(DimChestIntegration::new, BlockEntityDimChest.class);
    }
}
