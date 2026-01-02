package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        generator.addProvider(event.includeServer(), new BlockTagsProvider(packOutput, completablefuture, existingFileHelper, APRegistration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(packOutput, lookupProvider));
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> fullRegistryPatch = RegistryPatchGenerator.createLookup(event.getLookupProvider(), Util.make(new RegistrySetBuilder(), (builder) -> {
            builder.add(ITurtleUpgrade.REGISTRY, TurtleUpgradesProvider::addUpgrades);
            builder.add(IPocketUpgrade.REGISTRY, PocketUpgradesProvider::addUpgrades);
        }));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, fullRegistryPatch, null));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(packOutput, completablefuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsProvider(generator, existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(packOutput));
    }
}
