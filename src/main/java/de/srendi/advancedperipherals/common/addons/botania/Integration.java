package de.srendi.advancedperipherals.common.addons.botania;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ManaFlowerIntegration::new, GeneratingFlowerBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ManaPoolIntegration::new, ManaPoolBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SpreaderIntegration::new, ManaSpreaderBlockEntity.class);
    }
}
