package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.tileentity.TileEntity;

public class ChunkyPeripheral extends BasePeripheral {

    public ChunkyPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

}
