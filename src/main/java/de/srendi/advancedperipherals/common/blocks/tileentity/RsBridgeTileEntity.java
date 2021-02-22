package de.srendi.advancedperipherals.common.blocks.tileentity;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RsBridgeTileEntity extends PeripheralTileEntity<RsBridgePeripheral> implements INetworkNodeProxy<RefinedStorageNode>, IRedstoneConfigurable {

    private final LazyOptional<INetworkNodeProxy<RefinedStorageNode>> networkNodeProxy = LazyOptional.of(()->this);
    private RefinedStorageNode clientNode;
    private RefinedStorageNode removedNode;

    public RsBridgeTileEntity() {
        this(TileEntityTypes.RS_BRIDGE.get());
    }

    public RsBridgeTileEntity(TileEntityType tileType) {
        super(tileType);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        super.getCapability(cap, direction);

        return cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY ? this.networkNodeProxy.cast() : super.getCapability(cap, direction);
    }

    @Override
    protected RsBridgePeripheral createPeripheral() {
        return new RsBridgePeripheral("rsBridge", this);
    }

    public RefinedStorageNode createNode(World world, BlockPos blockPos) {
        return new RefinedStorageNode(world, blockPos);
    }

    //Adapted from com.refinedmods.refinedstorage.tileNetworkNodeTile
    @NotNull
    @Override
    public RefinedStorageNode getNode() {
        if (this.world.isRemote) {
            if (this.clientNode == null) {
                this.clientNode = this.createNode(this.world, this.pos);
            }

            return this.clientNode;
        } else {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) this.world);
            INetworkNode node = manager.getNode(this.pos);
            if (node == null) {
                throw new IllegalStateException("No network node present at " + this.pos.toString() + ", consider removing the block at this position");
            } else {
                return (RefinedStorageNode) node;
            }
        }
    }

    public void validate() {
        super.validate();
        if (!this.world.isRemote) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) this.world);
            if (manager.getNode(this.pos) == null) {
                manager.setNode(this.pos, this.createNode(this.world, this.pos));
                manager.markForSaving();
            }
        }
    }

    public void remove() {
        super.remove();
        if (!this.world.isRemote) {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) this.world);
            INetworkNode node = manager.getNode(this.pos);
            if (node != null) {
                this.removedNode = (RefinedStorageNode) node;
            }

            manager.removeNode(this.pos);
            manager.markForSaving();
            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getWorld(), node.getNetwork().getPosition());
            }
        }
    }

    public RedstoneMode getRedstoneMode() {
        return this.getNode().getRedstoneMode();
    }

    public void setRedstoneMode(RedstoneMode mode) {
        this.getNode().setRedstoneMode(mode);
    }
}