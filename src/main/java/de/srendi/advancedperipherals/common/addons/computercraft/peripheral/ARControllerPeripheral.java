package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class ARControllerPeripheral extends BasePeripheral{
	public ARControllerPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public ARControllerPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public ARControllerPeripheral(String type, Entity tileEntity) {
        super(type, tileEntity);
    }
    
    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableARGoggles;
    }
}
