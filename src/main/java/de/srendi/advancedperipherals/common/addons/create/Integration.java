package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerTileEntity;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BlazeBurnerIntegration::new, BlazeBurnerTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FluidTankIntegration::new, FluidTankTileEntity.class);
        // Disable until verified that it does not clash with the existing create CC integration
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ScrollValueBehaviourIntegration::new, KineticTileEntity.class, tile -> tile.getBehaviour(ScrollValueBehaviour.TYPE) != null, 10);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BasinIntegration::new, BasinTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MechanicalMixerIntegration::new, MechanicalMixerTileEntity.class);
    }
}
