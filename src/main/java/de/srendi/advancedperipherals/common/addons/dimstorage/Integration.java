package de.srendi.advancedperipherals.common.addons.dimstorage;

import dan200.computercraft.api.ComputerCraftAPI;

public class Integration implements Runnable {
    @Override
    public void run() {
        ComputerCraftAPI.registerGenericSource(new DimChestIntegration());
    }
}
