package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.ComputerCraftAPI;

public class Integration implements Runnable {

    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new ManaFlowerIntegration());
        ComputerCraftAPI.registerGenericSource(new ManaPoolIntegration());
        ComputerCraftAPI.registerGenericSource(new SpreaderIntegration());
    }
}
