package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.blocks.blockentities.FluidDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;

public class FluidDetectorPeripheral extends BaseDetectorPeripheral<FluidDetectorEntity> {

    public static final String TYPE = "fluid_detector";

    public FluidDetectorPeripheral(FluidDetectorEntity tileEntity) {
        super(TYPE, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableFluidDetector.get();
    }
}
