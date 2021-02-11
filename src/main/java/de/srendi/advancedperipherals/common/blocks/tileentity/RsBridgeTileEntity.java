package de.srendi.advancedperipherals.common.blocks.tileentity;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class RsBridgeTileEntity extends NetworkNodeTile<RefinedStorageNode> {

    private RefinedStorageNode node;
    private RsBridgePeripheral peripheral;
    private LazyOptional<IPeripheral> peripheralCap;

    public RsBridgeTileEntity() {
        this(ModTileEntityTypes.RS_BRIDGE.get());
    }

    public RsBridgeTileEntity(TileEntityType tileType) {
        super(tileType);
    }

    public List<IComputerAccess> getConnectedComputers() {
        return peripheral.getConnectedComputers();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(()->peripheral);
            }
            return peripheralCap.cast();
        }

        return super.getCapability(cap, direction);
    }

    @Override
    public RefinedStorageNode createNode(World world, BlockPos blockPos) {
        node = new RefinedStorageNode(world, blockPos);
        return node;
    }

    @Override
    public void validate() {
        peripheral = new RsBridgePeripheral(this);
        super.validate();
    }
}