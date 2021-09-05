package de.srendi.advancedperipherals.common.addons.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;

public class RedstoneProbeIntegration extends TileEntityIntegrationPeripheral<ConnectorProbeTileEntity> {

    public RedstoneProbeIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "redstoneProbe";
    }

    @LuaFunction(mainThread = true)
    public final String getReceivingChannel() {
        return tileEntity.redstoneChannel.toString();
    }

    @LuaFunction(mainThread = true)
    public final void setReceivingChannel(DyeColor color) {
        tileEntity.redstoneChannel = color;
        tileEntity.setChanged();
        GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        tileEntity.markContainingBlockForUpdate(null);
        tileEntity.getLevel().blockEvent(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction(mainThread = true)
    public final String getSendingChannel() {
        return tileEntity.redstoneChannelSending.toString();
    }

    @LuaFunction(mainThread = true)
    public final void setSendingChannel(DyeColor color) {
        tileEntity.redstoneChannelSending = color;
        tileEntity.setChanged();
        GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        tileEntity.markContainingBlockForUpdate(null);
        tileEntity.getLevel().blockEvent(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction(mainThread = true)
    public final int getRedstoneForChannel(DyeColor color) {
        return GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(color.getId());
    }
}
