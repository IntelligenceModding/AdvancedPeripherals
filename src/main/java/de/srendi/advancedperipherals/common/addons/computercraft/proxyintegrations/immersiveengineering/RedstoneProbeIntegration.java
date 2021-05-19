package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.item.DyeColor;

public class RedstoneProbeIntegration extends ProxyIntegration<ConnectorProbeTileEntity> {
    @Override
    protected Class<ConnectorProbeTileEntity> getTargetClass() {
        return ConnectorProbeTileEntity.class;
    }

    @Override
    public RedstoneProbeIntegration getNewInstance() {
        return new RedstoneProbeIntegration();
    }

    @Override
    protected String getName() {
        return "redstoneProbe";
    }

    @LuaFunction
    public final void setSendingChannel(DyeColor color) {
        getTileEntity().redstoneChannelSending = color;
        getTileEntity().markDirty();
        GlobalWireNetwork.getNetwork(getTileEntity().getWorld()).getLocalNet(getTileEntity().getPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        getTileEntity().markContainingBlockForUpdate(null);
        getTileEntity().getWorld().addBlockEvent(getTileEntity().getPos(), getTileEntity().getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public final void setReceivingChannel(DyeColor color) {
        getTileEntity().redstoneChannel = color;
        getTileEntity().markDirty();
        GlobalWireNetwork.getNetwork(getTileEntity().getWorld()).getLocalNet(getTileEntity().getPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        getTileEntity().markContainingBlockForUpdate(null);
        getTileEntity().getWorld().addBlockEvent(getTileEntity().getPos(), getTileEntity().getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public final String getReceivingChannel() {
        return getTileEntity().redstoneChannel.toString();
    }

    @LuaFunction
    public final String getSendingChannel() {
        return getTileEntity().redstoneChannelSending.toString();
    }
}
