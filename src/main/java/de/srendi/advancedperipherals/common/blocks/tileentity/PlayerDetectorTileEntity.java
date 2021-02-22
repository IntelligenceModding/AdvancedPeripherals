package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;

public class PlayerDetectorTileEntity extends PeripheralTileEntity<PlayerDetectorPeripheral> {

    public PlayerDetectorTileEntity() {
        this(TileEntityTypes.PLAYER_DETECTOR.get());
    }

    public PlayerDetectorTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral("playerDetector", this);
    }

}