package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.impl.Peripherals;

public class Integration implements Runnable {
    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new BasinIntegration());
        ComputerCraftAPI.registerGenericSource(new BlazeBurnerIntegration());
        ComputerCraftAPI.registerGenericSource(new FluidTankIntegration());
        ComputerCraftAPI.registerGenericSource(new ScrollValueBehaviourIntegration());

        Peripherals.addGenericLookup(new CreateBehaviourLookup(ScrollValueBehaviour.TYPE));
    }
}
