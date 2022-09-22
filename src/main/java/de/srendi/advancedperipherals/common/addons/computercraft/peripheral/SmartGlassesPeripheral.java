package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.SmartGlassesControllerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class SmartGlassesPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<SmartGlassesControllerEntity>> {

    public SmartGlassesPeripheral(SmartGlassesControllerEntity tileEntity) {
        super("smartglasses", new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableSmartGlasses.get();
    }

}
