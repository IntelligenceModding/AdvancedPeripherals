package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;

public class BlockReaderPeripheral extends BasePeripheral {

    public BlockReaderPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
