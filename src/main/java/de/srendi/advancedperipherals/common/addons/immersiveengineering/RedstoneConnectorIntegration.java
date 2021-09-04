package de.srendi.advancedperipherals.common.addons.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorRedstoneTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;

public class RedstoneConnectorIntegration extends TileEntityIntegrationPeripheral<ConnectorRedstoneTileEntity> {

    public RedstoneConnectorIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "redstoneConnector";
    }

    @LuaFunction(mainThread = true)
    public final String getRedstoneChannel() {
        return tileEntity.redstoneChannel.toString();
    }

    @LuaFunction(mainThread = true)
    public final void setRedstoneChannel(DyeColor color) {
        tileEntity.redstoneChannel = color;
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

    @LuaFunction(mainThread = true)
    public final boolean isInputMode() {
        return tileEntity.isRSInput();
    }

    @LuaFunction(mainThread = true)
    public final int getOutput() {
        return GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(tileEntity.redstoneChannel.getId());
    }

}
