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
package de.srendi.advancedperipherals.common.addons.powah;

import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import owmii.powah.block.ender.EnderCellTile;
import owmii.powah.block.energycell.EnergyCellTile;
import owmii.powah.block.furnator.FurnatorTile;
import owmii.powah.block.magmator.MagmatorTile;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.solar.SolarTile;
import owmii.powah.block.thermo.ThermoTile;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ReactorIntegration::new, ReactorPartTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(EnergyCellIntegration::new, EnergyCellTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(EnderCellIntegration::new, EnderCellTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(SolarPanelIntegration::new, SolarTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(FurnatorIntegration::new, FurnatorTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(MagmatorIntegration::new, MagmatorTile.class);
        IntegrationPeripheralProvider.registerBlockEntityIntegration(ThermoIntegration::new, ThermoTile.class);
    }
}
