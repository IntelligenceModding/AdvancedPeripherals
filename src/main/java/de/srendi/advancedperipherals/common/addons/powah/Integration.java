package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.ComputerCraftAPI;

public class Integration implements Runnable {

    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new ReactorIntegration());
        ComputerCraftAPI.registerGenericSource(new EnergyCellIntegration());
        ComputerCraftAPI.registerGenericSource(new EnderCellIntegration());
        ComputerCraftAPI.registerGenericSource(new SolarPanelIntegration());
        ComputerCraftAPI.registerGenericSource(new FurnatorIntegration());
        ComputerCraftAPI.registerGenericSource(new MagmatorIntegration());
        ComputerCraftAPI.registerGenericSource(new ThermoIntegration());
    }
}
