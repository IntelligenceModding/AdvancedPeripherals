package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ProxyIntegrationRegistry {

    public static final List<ProxyIntegration<?>> integrations = new ArrayList<>();

    public static void registerIntegration(ProxyIntegration<?> integration) {
        integrations.add(integration);
    }

    public static void register() {
        //It's vanilla, so the condition is always true
        registerIntegration(new BeaconIntegration());
        if(ModList.get().isLoaded("mekanismgenerators")) {
            registerIntegration(new FissionIntegration());
            registerIntegration(new FusionIntegration());
            registerIntegration(new TurbineIntegration());
        }
        if(ModList.get().isLoaded("mekanism")) {
            registerIntegration(new InductionPortIntegration());
            registerIntegration(new BoilerIntegration());
            registerIntegration(new GenericMekanismIntegration());
        }
        if(ModList.get().isLoaded("botania")) {
            registerIntegration(new ManaPoolIntegration());
            registerIntegration(new SpreaderIntegration());
            registerIntegration(new ManaFlowerIntegration());
        }
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
