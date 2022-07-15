package de.srendi.advancedperipherals.common.addons.botania;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.block.tile.mana.TileSpreader;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ManaFlowerIntegration::new, TileEntityGeneratingFlower.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ManaPoolIntegration::new, TilePool.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SpreaderIntegration::new, TileSpreader.class);
    }
}
