package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.pocket.PocketUpgradeDataProvider;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketColonyIntegratorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketDistanceDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironmentUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketInventoryManagerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PocketUpgradesProvider extends PocketUpgradeDataProvider {
    public PocketUpgradesProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addUpgrades(Consumer<Upgrade<PocketUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(CCRegistration.ID.Pocket.CHATTY, CCRegistration.CHAT_BOX_POCKET.get(), APBlocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.COLONY, CCRegistration.COLONY_POCKET.get(), APBlocks.COLONY_INTEGRATOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.DISTANCE, CCRegistration.DISTANCE_DETECTOR_POCKET.get(), APBlocks.DISTANCE_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.ENVIRONMENT, CCRegistration.ENVIRONMENT_POCKET.get(), APBlocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.GEOSCANNER, CCRegistration.GEO_SCANNER_POCKET.get(), APBlocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.INVENTORY_MANAGER, CCRegistration.INVENTORY_MANAGER_POCKET.get(), APBlocks.INVENTORY_MANAGER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Pocket.PLAYER, CCRegistration.PLAYER_DETECTOR_POCKET.get(), APBlocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
    }
}
