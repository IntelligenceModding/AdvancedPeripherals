package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import net.minecraft.item.DyeColor;

public class RedstoneProbeIntegration extends Integration<ConnectorProbeTileEntity> {
    @Override
    protected Class<ConnectorProbeTileEntity> getTargetClass() {
        return ConnectorProbeTileEntity.class;
    }

    @Override
    public RedstoneProbeIntegration getNewInstance() {
        return new RedstoneProbeIntegration();
    }

    @Override
    public String getType() {
        return "redstoneProbe";
    }

    @LuaFunction
    public final String getReceivingChannel() {
        return getTileEntity().redstoneChannel.toString();
    }

    @LuaFunction
    public final void setReceivingChannel(DyeColor color) {
        getTileEntity().redstoneChannel = color;
        getTileEntity().setChanged();
        GlobalWireNetwork.getNetwork(getTileEntity().getLevel()).getLocalNet(getTileEntity().getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        getTileEntity().markContainingBlockForUpdate(null);
        getTileEntity().getLevel().blockEvent(getTileEntity().getBlockPos(), getTileEntity().getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public final String getSendingChannel() {
        return getTileEntity().redstoneChannelSending.toString();
    }

    @LuaFunction
    public final void setSendingChannel(DyeColor color) {
        getTileEntity().redstoneChannelSending = color;
        getTileEntity().setChanged();
        GlobalWireNetwork.getNetwork(getTileEntity().getLevel()).getLocalNet(getTileEntity().getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        getTileEntity().markContainingBlockForUpdate(null);
        getTileEntity().getLevel().blockEvent(getTileEntity().getBlockPos(), getTileEntity().getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public final int getRedstoneForChannel(DyeColor color) {
        return GlobalWireNetwork.getNetwork(getTileEntity().getLevel()).getLocalNet(getTileEntity().getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(color.getId());
    }
}
