package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketColonyIntegratorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketDistanceDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironmentUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketInventoryManagerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PocketUpgradesProvider {

    public static void addUpgrades(BootstrapContext<IPocketUpgrade> upgrades) {
        upgrades.register(id(CCRegistration.ID.CHATTY_POCKET), new PocketChatBoxUpgrade(new ItemStack(APBlocks.CHAT_BOX.get())));
        upgrades.register(id(CCRegistration.ID.COLONY_POCKET), new PocketColonyIntegratorUpgrade(new ItemStack(APBlocks.COLONY_INTEGRATOR.get())));
        upgrades.register(id(CCRegistration.ID.DISTANCE_POCKET), new PocketDistanceDetectorUpgrade(new ItemStack(APBlocks.DISTANCE_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.ENVIRONMENT_POCKET), new PocketEnvironmentUpgrade(new ItemStack(APBlocks.ENVIRONMENT_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.GEOSCANNER_POCKET), new PocketGeoScannerUpgrade(new ItemStack(APBlocks.GEO_SCANNER.get())));
        upgrades.register(id(CCRegistration.ID.INVENTORY_MANAGER_POCKET), new PocketInventoryManagerUpgrade(new ItemStack(APBlocks.INVENTORY_MANAGER.get())));
        upgrades.register(id(CCRegistration.ID.PLAYER_POCKET), new PocketPlayerDetectorUpgrade(new ItemStack(APBlocks.PLAYER_DETECTOR.get())));
    }

    public static ResourceKey<IPocketUpgrade> id(ResourceLocation id) {
        return ResourceKey.create(IPocketUpgrade.REGISTRY, id);
    }
}
