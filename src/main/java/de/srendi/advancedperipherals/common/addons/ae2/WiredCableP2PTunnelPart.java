package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.IGridNodeListener;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.IWiredElement;
import dan200.computercraft.api.network.wired.IWiredNetworkChange;
import dan200.computercraft.api.network.wired.IWiredNode;
import dan200.computercraft.shared.Capabilities;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class WiredCableP2PTunnelPart extends CapabilityP2PTunnelPart<WiredCableP2PTunnelPart, IWiredElement> {
    private static final P2PModels MODELS = new P2PModels(AdvancedPeripherals.getRL("part/p2p/p2p_tunnel_cable"));

    private final IWiredElement element = new P2PWiredElement();
    private final IWiredElement outElement = new P2PWiredElement();
    private final IWiredNode node = this.element.getNode();
    private Set<WiredCableP2PTunnelPart> connected = new HashSet<>();
    private boolean activated = false;

    public WiredCableP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, Capabilities.CAPABILITY_WIRED_ELEMENT);
        this.inputHandler = outElement;
        this.outputHandler = outElement;
        this.emptyHandler = null; // should never used
    }

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    @Override
    public void onTunnelConfigChange() {
        super.onTunnelConfigChange();
        this.connectionsChanged();
    }

    @Override
    public void onTunnelNetworkChange() {
        super.onTunnelNetworkChange();
        this.connectionsChanged();
    }

    protected void connectionsChanged() {
        if (this.isClientSide()) {
            return;
        }
        if (!this.isActive()) {
            return;
        }
        if (!this.activated) {
            this.activated = true;
            this.node.connectTo(this.outElement.getNode());
        }

        Stream<WiredCableP2PTunnelPart> nodeStream = this.getOutputStream().filter(out -> out != this);
        WiredCableP2PTunnelPart in = this.getInput();
        if (in != null && in != this) {
            nodeStream = Stream.concat(nodeStream, Stream.of(in));
        }
        Set<WiredCableP2PTunnelPart> nodes = nodeStream.collect(Collectors.toCollection(HashSet::new));

        for (WiredCableP2PTunnelPart part : this.connected.stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList())) {
            if (part.connected.contains(this)) {
                this.node.disconnectFrom(part.node);
                part.connected.remove(this);
            }
            this.connected.remove(part);
        }

        for (WiredCableP2PTunnelPart part : nodes) {
            if (!this.connected.contains(part)) {
                this.node.connectTo(part.node);
                this.connected.add(part);
                part.connected.add(this);
            }
        }
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        if (reason == IGridNodeListener.State.GRID_BOOT) {
            return;
        }
        if (this.isActive()) {
            if (!this.getMainNode().hasGridBooted()) {
                return;
            }
            this.connectionsChanged();
            this.refreshConnection();
        } else if (this.activated) {
            this.activated = false;
            this.node.remove();
            this.connected.clear();
        }
    }

    protected BlockPos getFacingPos() {
        return this.getHost().getLocation().getPos().relative(this.getSide());
    }

    protected void refreshConnection() {
        BlockEntity cable = this.getLevel().getBlockEntity(this.getFacingPos());
        IWiredElement elem = cable == null ? null : cable.getCapability(Capabilities.CAPABILITY_WIRED_ELEMENT, this.getSide().getOpposite()).orElse(null);
        if (elem == null) {
            return;
        }
        elem.getNode().connectTo(this.outElement.getNode());
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (!this.getFacingPos().equals(neighbor)) {
            return;
        }
        if (this.activated) {
            this.refreshConnection();
        }
    }

    private class P2PWiredElement implements IWiredElement {
        private final IWiredNode node = ComputerCraftAPI.createWiredNodeForElement(this);

        @Nonnull
        @Override
        public IWiredNode getNode() {
            return node;
        }

        @Nonnull
        @Override
        public String getSenderID() {
            return "p2p";
        }

        @Nonnull
        @Override
        public Level getLevel() {
            return WiredCableP2PTunnelPart.this.getLevel();
        }

        @Nonnull
        @Override
        public Vec3 getPosition() {
            return Vec3.atCenterOf(WiredCableP2PTunnelPart.this.getBlockEntity().getBlockPos());
        }

        @Override
        public void networkChanged(IWiredNetworkChange change) {}
    }
}
