package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import org.jetbrains.annotations.NotNull;

public class ColonyIntegratorTile extends PeripheralTileEntity<ColonyPeripheral> {

    public ColonyIntegratorTile() {
        super(TileEntityTypes.COLONY_INTEGRATOR.get());
    }

    @NotNull
    @Override
    protected ColonyPeripheral createPeripheral() {
        return new ColonyPeripheral("colonyIntegrator", this);
    }
}
