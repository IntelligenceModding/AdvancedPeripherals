package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

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