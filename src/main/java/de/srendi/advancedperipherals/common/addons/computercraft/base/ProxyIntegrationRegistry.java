package de.srendi.advancedperipherals.common.addons.computercraft.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.BeaconIntegration;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProxyIntegrationRegistry {

    public static final List<ProxyIntegration> integrations = new ArrayList<>();

    public static void registerIntegration(ProxyIntegration integration) {
        integrations.add(integration);
    }

    public static void register() {
        registerIntegration(new BeaconIntegration());
    }

    public static ProxyIntegration getIntegration(TileEntity tileEntity) {
        for(ProxyIntegration integration : integrations) {
            AdvancedPeripherals.debug("DEBUG1 " + integration);
            if(integration.isTileEntity(tileEntity)) {
                AdvancedPeripherals.debug("DEBUG2 " + integration);
                return integration.getNewInstance();
            }
        }
        return null;
    }

}
