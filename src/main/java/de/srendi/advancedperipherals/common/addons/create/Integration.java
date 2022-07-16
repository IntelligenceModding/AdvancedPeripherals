package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerTileEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerTileEntity;
import com.simibubi.create.content.contraptions.relays.advanced.SpeedControllerTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BlazeBurnerIntegration::new, BlazeBurnerTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SpeedControllerIntegration::new, SpeedControllerTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FluidTankIntegration::new, FluidTankTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(CreativeMotorIntegration::new, CreativeMotorTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BasinIntegration::new, BasinTileEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MechanicalMixerIntegration::new, MechanicalMixerTileEntity.class);

    }
}
