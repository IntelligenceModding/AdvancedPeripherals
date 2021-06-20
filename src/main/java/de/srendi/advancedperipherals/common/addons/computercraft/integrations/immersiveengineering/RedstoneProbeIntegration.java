package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RedstoneProbeIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "redstoneProbe");
    }

    @LuaFunction
    public static String getReceivingChannel(ConnectorProbeTileEntity tileEntity) {
        return tileEntity.redstoneChannel.toString();
    }

    @LuaFunction
    public static void setReceivingChannel(ConnectorProbeTileEntity tileEntity, DyeColor color) {
        tileEntity.redstoneChannel = color;
        tileEntity.setChanged();
        GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        tileEntity.markContainingBlockForUpdate(null);
        tileEntity.getLevel().blockEvent(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public static String getSendingChannel(ConnectorProbeTileEntity tileEntity) {
        return tileEntity.redstoneChannelSending.toString();
    }

    @LuaFunction
    public static void setSendingChannel(ConnectorProbeTileEntity tileEntity, DyeColor color) {
        tileEntity.redstoneChannelSending = color;
        tileEntity.setChanged();
        GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        tileEntity.markContainingBlockForUpdate(null);
        tileEntity.getLevel().blockEvent(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public static int getRedstoneForChannel(ConnectorProbeTileEntity tileEntity, DyeColor color) {
        return GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(color.getId());
    }
}
