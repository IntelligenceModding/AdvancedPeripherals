package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorRedstoneTileEntity;
import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RedstoneConnectorIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "redstoneConnector");
    }

    @LuaFunction
    public static String getRedstoneChannel(ConnectorRedstoneTileEntity tileEntity) {
        return tileEntity.redstoneChannel.toString();
    }

    @LuaFunction
    public static void setRedstoneChannel(ConnectorRedstoneTileEntity tileEntity, DyeColor color) {
        tileEntity.redstoneChannel = color;
        tileEntity.setChanged();
        GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos()).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).updateValues();
        tileEntity.markContainingBlockForUpdate(null);
        tileEntity.getLevel().blockEvent(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock(), 254, 0);
    }

    @LuaFunction
    public static int getRedstoneForChannel(ConnectorRedstoneTileEntity tileEntity, DyeColor color) {
        return GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(color.getId());
    }

    @LuaFunction
    public static boolean isInputMode(ConnectorRedstoneTileEntity tileEntity) {
        return tileEntity.isRSInput();
    }

    @LuaFunction
    public static int getOutput(ConnectorRedstoneTileEntity tileEntity) {
        return GlobalWireNetwork.getNetwork(tileEntity.getLevel()).getLocalNet(tileEntity.getBlockPos())
                .getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class).getValue(tileEntity.redstoneChannel.getId());
    }

}
