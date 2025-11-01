package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;

public class EnergyDetectorPeripheral extends BaseDetectorPeripheral<EnergyDetectorEntity> {

    public static final String PERIPHERAL_TYPE = "energy_detector";

    public EnergyDetectorPeripheral(EnergyDetectorEntity tileEntity) {
        super(PERIPHERAL_TYPE, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableEnergyDetector.get();
    }
}
