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
package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import net.minecraftforge.common.ForgeConfigSpec;

public enum AutomataCoreTier implements IAutomataCoreTier {
    TIER1(2, 2), TIER2(4, 3), OVERPOWERED_TIER1(4, 3), OVERPOWERED_TIER2(6, 4);

    private final int defaultInteractionRadius;
    private final int defaultMaxFuelConsumptionRate;
    private ForgeConfigSpec.IntValue interactionRadius;
    private ForgeConfigSpec.IntValue maxFuelConsumptionRate;

    AutomataCoreTier(int defaultInteractionRadius, int defaultMaxFuelConsumptionRate) {
        this.defaultInteractionRadius = defaultInteractionRadius;
        this.defaultMaxFuelConsumptionRate = defaultMaxFuelConsumptionRate;
    }

    @Override
    public int getInteractionRadius() {
        if (interactionRadius == null)
            return 0;
        return interactionRadius.get();
    }

    @Override
    public int getMaxFuelConsumptionRate() {
        if (maxFuelConsumptionRate == null)
            return 0;
        return maxFuelConsumptionRate.get();
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        interactionRadius = builder.defineInRange(settingsName() + "InteractionRadius", defaultInteractionRadius, 1,
                64);
        maxFuelConsumptionRate = builder.defineInRange(settingsName() + "MaxFuelConsumptionRate",
                defaultMaxFuelConsumptionRate, 1, 32);
    }

    @Override
    public String settingsPostfix() {
        return "AutomataCore";
    }

}
