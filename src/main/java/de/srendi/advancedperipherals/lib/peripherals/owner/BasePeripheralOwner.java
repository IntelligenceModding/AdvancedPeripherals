package de.srendi.advancedperipherals.lib.peripherals.owner;

import de.srendi.advancedperipherals.api.peripherals.owner.IOwnerAbility;
import de.srendi.advancedperipherals.api.peripherals.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.api.peripherals.owner.PeripheralOwnerAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePeripheralOwner implements IPeripheralOwner {
    private final Map<PeripheralOwnerAbility<?>, IOwnerAbility> abilities;

    public BasePeripheralOwner() {
        abilities = new HashMap<>();
    }

    @Override
    public <T extends IOwnerAbility> void attachAbility(PeripheralOwnerAbility<T> ability, T abilityImplementation) {
        abilities.put(ability, abilityImplementation);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T extends IOwnerAbility> T getAbility(PeripheralOwnerAbility<T> ability) {
        return (T) abilities.get(ability);
    }

    @Override
    public Collection<IOwnerAbility> getAbilities() {
        return abilities.values();
    }
}
