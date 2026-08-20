package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.impl.Peripherals;

public class Integration implements Runnable {
    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new BasinIntegration());
        ComputerCraftAPI.registerGenericSource(new BlazeBurnerIntegration());
        ComputerCraftAPI.registerGenericSource(new FluidTankIntegration());

        Peripherals.registerGenericLookup(new CreateBehaviourLookup<>(FilteringBehaviour.TYPE, FilteringBehaviourIntegration.Wrapper::new));
        ComputerCraftAPI.registerGenericSource(new FilteringBehaviourIntegration());

        Peripherals.registerGenericLookup(new CreateBehaviourLookup<>(ScrollValueBehaviour.TYPE, null));
        ComputerCraftAPI.registerGenericSource(new ScrollValueBehaviourIntegration());
    }
}
