package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorRedstoneTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.item.DyeColor;

public class RedstoneConnectorIntegration extends ProxyIntegration<ConnectorRedstoneTileEntity> {
    @Override
    protected Class<ConnectorRedstoneTileEntity> getTargetClass() {
        return ConnectorRedstoneTileEntity.class;
    }

    @Override
    public RedstoneConnectorIntegration getNewInstance() {
        return new RedstoneConnectorIntegration();
    }

    @Override
    protected String getName() {
        return "redstoneConnector";
    }

    @LuaFunction
    public final void setRedstoneChannel(DyeColor color) {
        getTileEntity().redstoneChannel = color;
        getTileEntity().markDirty();
        GlobalWireNetwork.getNetwork(getTileEntity().getWorld()).getLocalNet(getTileEntity().getPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        getTileEntity().markContainingBlockForUpdate(null);
        getTileEntity().getWorld().addBlockEvent(getTileEntity().getPos(), getTileEntity().getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public final String getRedstoneChannel() {
        return getTileEntity().redstoneChannel.toString();
    }

    @LuaFunction
    public final int getRedstoneForChannel(DyeColor color) {
        return GlobalWireNetwork.getNetwork(getTileEntity().getWorld()).getLocalNet(getTileEntity().getPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(color.getId());

    }

    @LuaFunction
    public final boolean isInputMode() {
        return getTileEntity().isRSInput();
    }

    @LuaFunction
    public final int getOutput() {
        return GlobalWireNetwork.getNetwork(getTileEntity().getWorld()).getLocalNet(getTileEntity().getPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(getTileEntity().redstoneChannel.getId());
    }

}
