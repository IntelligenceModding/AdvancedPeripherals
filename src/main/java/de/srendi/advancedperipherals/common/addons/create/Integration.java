package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BlazeBurnerIntegration::new, BlazeBurnerBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FluidTankIntegration::new, FluidTankBlockEntity.class);
        // Disable until verified that it does not clash with the existing create CC integration
        //IntegrationPeripheralProvider.registerBlockEntityIntegration(ScrollValueBehaviourIntegration::new, KineticTileEntity.class, tile -> tile.getBehaviour(ScrollValueBehaviour.TYPE) != null, 10);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BasinIntegration::new, BasinBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MechanicalMixerIntegration::new, MechanicalMixerBlockEntity.class);
    }
}
