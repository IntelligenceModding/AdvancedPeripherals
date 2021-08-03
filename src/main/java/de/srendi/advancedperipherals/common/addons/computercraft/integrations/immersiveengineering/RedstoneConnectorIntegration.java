package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorRedstoneTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import net.minecraft.world.item.DyeColor;

public class RedstoneConnectorIntegration extends Integration<ConnectorRedstoneTileEntity> {
    @Override
    protected Class<ConnectorRedstoneTileEntity> getTargetClass() {
        return ConnectorRedstoneTileEntity.class;
    }

    @Override
    public RedstoneConnectorIntegration getNewInstance() {
        return new RedstoneConnectorIntegration();
    }

    @Override
    public String getType() {
        return "redstoneConnector";
    }

    @LuaFunction
    public final String getRedstoneChannel() {
        return getTileEntity().redstoneChannel.toString();
    }

    @LuaFunction(mainThread = true)
    public final void setRedstoneChannel(DyeColor color) {
        getTileEntity().redstoneChannel = color;
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

    @LuaFunction
    public final boolean isInputMode() {
        return getTileEntity().isRSInput();
    }

    @LuaFunction
    public final int getOutput() {
        return GlobalWireNetwork.getNetwork(getTileEntity().getLevel()).getLocalNet(getTileEntity().getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(getTileEntity().redstoneChannel.getId());
    }

}
