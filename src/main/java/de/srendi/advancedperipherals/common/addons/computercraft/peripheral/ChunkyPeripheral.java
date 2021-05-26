package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.tileentity.TileEntity;

public class ChunkyPeripheral extends BasePeripheral {

    public ChunkyPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

}
