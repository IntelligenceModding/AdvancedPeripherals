package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.tileentity.BeaconTileEntity;
import org.jetbrains.annotations.NotNull;

public class BeaconIntegration extends ProxyIntegration {
    @Override
    public Class<?> getTargetClass() {
        return BeaconTileEntity.class;
    }

    @Override
    protected String getName() {
        return "beacon";
    }

    @LuaFunction
    public final String test() {
        return "Well, it works... i think";
    }

    @Override
    public BeaconIntegration getNewInstance() {
        return new BeaconIntegration();
    }
}
