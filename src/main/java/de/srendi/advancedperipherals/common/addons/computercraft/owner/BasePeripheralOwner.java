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

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePeripheralOwner implements IPeripheralOwner {
    private final Map<PeripheralOwnerAbility<?>, IOwnerAbility> abilities;

    protected BasePeripheralOwner() {
        abilities = new HashMap<>();
    }

    @Override
    public <T extends IOwnerAbility> void attachAbility(PeripheralOwnerAbility<T> ability, T abilityImplementation) {
        abilities.put(ability, abilityImplementation);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable public <T extends IOwnerAbility> T getAbility(PeripheralOwnerAbility<T> ability) {
        return (T) abilities.get(ability);
    }

    @Override
    public Collection<IOwnerAbility> getAbilities() {
        return abilities.values();
    }
}
