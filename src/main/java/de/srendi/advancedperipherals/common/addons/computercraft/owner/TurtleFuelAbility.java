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

public class TurtleFuelAbility extends FuelAbility<TurtlePeripheralOwner> {
    private final int maxFuelConsumptionLevel;

    public TurtleFuelAbility(@NotNull TurtlePeripheralOwner owner, int maxFuelConsumptionLevel) {
        super(owner);
        this.maxFuelConsumptionLevel = maxFuelConsumptionLevel;
    }

    @Override
    protected boolean consumeFuel(int count) {
        return owner.turtle.consumeFuel(count);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return maxFuelConsumptionLevel;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return !owner.getTurtle().isFuelNeeded();
    }

    @Override
    public int getFuelCount() {
        return owner.turtle.getFuelLevel();
    }

    @Override
    public int getFuelMaxCount() {
        return owner.turtle.getFuelLimit();
    }

    @Override
    public void addFuel(int count) {
        owner.turtle.addFuel(count);
    }
}
