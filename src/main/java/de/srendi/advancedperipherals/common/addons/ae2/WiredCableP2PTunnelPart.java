package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;
import dan200.computercraft.api.network.wired.IWiredElement;
import dan200.computercraft.api.network.wired.IWiredNode;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemElement;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public class WiredCableP2PTunnelPart extends CapabilityP2PTunnelPart<WiredCableP2PTunnelPart, IWiredElement> {
    private static final P2PModels MODELS = new P2PModels(AdvancedPeripherals.getRL("part/p2p/p2p_tunnel_cable"));

    private final P2PWiredElement element = new P2PWiredElement();
    private final IWiredNode node = this.element.getNode();
    private final Set<IWiredNode> connected = new HashSet<>();
    private short lastFreq = 0;

    public WiredCableP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, Capabilities.CAPABILITY_WIRED_ELEMENT);
        this.inputHandler = element;
        this.outputHandler = element;
        this.emptyHandler = element;
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
        this.connectionsChanged();
    }

    @Override
    public void onTunnelNetworkChange() {
        this.connectionsChanged();
    }

    protected void connectionsChanged() {
        if (this.lastFreq == this.getFrequency()) {
            return;
        }
        this.lastFreq = this.getFrequency();

        for (IWiredNode node : this.connected) {
            this.node.disconnectFrom(node);
        }
        this.connected.clear();

        WiredCableP2PTunnelPart in = this.getInput();
        if (in != null && in != this) {
            this.node.connectTo(in.node);
            this.connected.add(in.node);
        }
        for (WiredCableP2PTunnelPart out : WiredCableP2PTunnelPart.this.getOutputs()) {
            if (out != this) {
                this.node.connectTo(out.node);
                this.connected.add(out.node);
            }
        }
    }

    private class P2PWiredElement extends WiredModemElement {
        private boolean updated = false;

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

        @Nonnull
        @Override
        public String getSenderID() {
            return "p2p";
        }

        @Override
        protected void attachPeripheral(String name, IPeripheral peripheral) {
            if (this.updated) {
                return;
            }
            this.updated = true;
            try {
                WiredCableP2PTunnelPart.this.connectionsChanged();
                WiredCableP2PTunnelPart in = WiredCableP2PTunnelPart.this.getInput();
                if (in != null) {
                    in.element.attachPeripheral(name, peripheral);
                }
                for (WiredCableP2PTunnelPart out : WiredCableP2PTunnelPart.this.getOutputs()) {
                    out.element.attachPeripheral(name, peripheral);
                }
            } finally {
                this.updated = false;
            }
        }

        @Override
        protected void detachPeripheral(String name) {
            if (this.updated) {
                return;
            }
            this.updated = true;
            try {
                WiredCableP2PTunnelPart.this.connectionsChanged();
                WiredCableP2PTunnelPart in = WiredCableP2PTunnelPart.this.getInput();
                if (in != null) {
                    in.element.detachPeripheral(name);
                }
                for (WiredCableP2PTunnelPart out : WiredCableP2PTunnelPart.this.getOutputs()) {
                    out.element.detachPeripheral(name);
                }
            } finally {
                this.updated = false;
            }
        }
    }
}
