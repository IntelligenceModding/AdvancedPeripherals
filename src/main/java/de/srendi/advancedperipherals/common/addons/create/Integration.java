/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;

public class Integration implements Runnable {

    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BlazeBurnerIntegration::new,
                BlazeBurnerBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FluidTankIntegration::new,
                FluidTankBlockEntity.class);
        // Disable until verified that it does not clash with the existing create CC
        // integration
        // IntegrationPeripheralProvider.registerBlockEntityIntegration(ScrollValueBehaviourIntegration::new,
        // KineticTileEntity.class, tile -> tile.getBehaviour(ScrollValueBehaviour.TYPE)
        // != null, 10);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(BasinIntegration::new, BasinBlockEntity.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MechanicalMixerIntegration::new,
                MechanicalMixerBlockEntity.class);
    }
}
