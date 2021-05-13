package de.srendi.advancedperipherals.common.addons.computercraft.base;

import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ProxyIntegrationRegistry {

    public static final List<ProxyIntegration<?>> integrations = new ArrayList<>();

    public static void registerIntegration(boolean condition, ProxyIntegration<?> integration) {
        if (condition)
            integrations.add(integration);
    }

    public static void register() {
        //It's vanilla, so the condition is always true
        registerIntegration(true, new BeaconIntegration());
        registerIntegration(ModList.get().isLoaded("mekanism"), new InductionPortIntegration());
        registerIntegration(ModList.get().isLoaded("mekanismgenerators"), new TurbineIntegration());
        registerIntegration(ModList.get().isLoaded("mekanism"), new BoilerIntegration());
        registerIntegration(ModList.get().isLoaded("mekanismgenerators"), new FissionIntegration());
        registerIntegration(ModList.get().isLoaded("mekanism"), new GenericMekanismIntegration());
    }

    public static ProxyIntegration<?> getIntegration(TileEntity tileEntity) {
        for (ProxyIntegration<?> integration : integrations) {
            if (integration.isTileEntity(tileEntity)) {
                return integration.getNewInstance();
            }
        }
        return null;
    }

}
