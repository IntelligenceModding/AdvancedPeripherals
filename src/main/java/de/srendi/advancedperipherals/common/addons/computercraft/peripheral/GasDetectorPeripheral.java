package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;

public class GasDetectorPeripheral extends BaseDetectorPeripheral<GasDetectorEntity> {

    public static final String TYPE = "gas_detector";

    public GasDetectorPeripheral(GasDetectorEntity tileEntity) {
        super(TYPE, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableGasDetector.get();
    }
}
