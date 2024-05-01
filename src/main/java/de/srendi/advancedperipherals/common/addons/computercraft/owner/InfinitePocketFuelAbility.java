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
package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import org.jetbrains.annotations.NotNull;

/**
 * This is a copy of the FuelAbility class, but with the fuel consumption
 * disabled. This is used for the Pocket Computer.
 */
public class InfinitePocketFuelAbility extends FuelAbility<PocketPeripheralOwner> {

    public InfinitePocketFuelAbility(@NotNull PocketPeripheralOwner owner) {
        super(owner);
    }

    @Override
    protected boolean consumeFuel(int count) {
        return true;
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return true;
    }

    @Override
    public int getFuelCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getFuelMaxCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void addFuel(int count) {
        // Not needed for infinite fuel
    }
}
