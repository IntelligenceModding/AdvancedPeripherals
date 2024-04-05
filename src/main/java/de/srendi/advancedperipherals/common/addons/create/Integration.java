package de.srendi.advancedperipherals.common.addons.create;

import dan200.computercraft.api.ComputerCraftAPI;

public class Integration implements Runnable {

    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new BlazeBurnerIntegration());
        ComputerCraftAPI.registerGenericSource(new FluidTankIntegration());
        // Disable until verified that it does not clash with the existing create CC integration
        //IntegrationPeripheralProvider.registerBlockEntityIntegration(ScrollValueBehaviourIntegration::new, KineticTileEntity.class, tile -> tile.getBehaviour(ScrollValueBehaviour.TYPE) != null, 10);
        ComputerCraftAPI.registerGenericSource(new BasinIntegration());
        ComputerCraftAPI.registerGenericSource(new MechanicalMixerIntegration());
    }
}
