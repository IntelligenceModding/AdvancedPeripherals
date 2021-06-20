package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.BeaconIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaFlowerIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaPoolIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.SpreaderIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.CapacitorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneConnectorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneProbeIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism.*;
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
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());
        if (ModList.get().isLoaded("mekanismgenerators")) {
            ComputerCraftAPI.registerGenericSource(new FissionIntegration());
            ComputerCraftAPI.registerGenericSource(new FusionIntegration());
            ComputerCraftAPI.registerGenericSource(new TurbineIntegration());
        }
        if (ModList.get().isLoaded("mekanism")) {
            ComputerCraftAPI.registerGenericSource(new InductionPortIntegration());
            ComputerCraftAPI.registerGenericSource(new BoilerIntegration());
            ComputerCraftAPI.registerGenericSource(new DigitalMinerIntegration());
            ComputerCraftAPI.registerGenericSource(new ChemicalTankIntegration());
            ComputerCraftAPI.registerGenericSource(new GenericMekanismIntegration());
        }
        if (ModList.get().isLoaded("botania")) {
            ComputerCraftAPI.registerGenericSource(new ManaPoolIntegration());
            ComputerCraftAPI.registerGenericSource(new SpreaderIntegration());
            ComputerCraftAPI.registerGenericSource(new ManaFlowerIntegration());
        }
        if (ModList.get().isLoaded("immersiveengineering")) {
            ComputerCraftAPI.registerGenericSource(new RedstoneProbeIntegration());
            ComputerCraftAPI.registerGenericSource(new RedstoneConnectorIntegration());
            ComputerCraftAPI.registerGenericSource(new CapacitorIntegration());
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
