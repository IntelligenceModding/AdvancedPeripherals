package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakAutomataCorePeripheral;

public class Integration implements Runnable {

    @Override
    public void run() {
        EnvironmentDetectorPeripheral.addIntegrationPlugin(ShipScannerPlugin::new);
        WeakAutomataCorePeripheral.addIntegrationPlugin(AutomataVSMountPlugin::new);
    }
}
