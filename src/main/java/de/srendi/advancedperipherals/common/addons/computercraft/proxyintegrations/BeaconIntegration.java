package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.tileentity.BeaconTileEntity;
import org.jetbrains.annotations.NotNull;

public class BeaconIntegration extends ProxyIntegration<BeaconTileEntity> {
    @Override
    public Class<BeaconTileEntity> getTargetClass() {
        return BeaconTileEntity.class;
    }

    @Override
    public BeaconIntegration getNewInstance() {
        return new BeaconIntegration();
    }

    @Override
    protected String getName() {
        return "beacon";
    }

    @LuaFunction
    public final int getLevel() {
        return getTileEntity().getLevels();
    }

    @LuaFunction
    public final String getPrimaryEffect() {
        return getTileEntity().primaryEffect == null ? "none" : getTileEntity().primaryEffect.getName();
    }

    @LuaFunction
    public final String getSecondaryEffect() {
        return getTileEntity().secondaryEffect == null ? "none" : getTileEntity().secondaryEffect.getName();
    }

}
