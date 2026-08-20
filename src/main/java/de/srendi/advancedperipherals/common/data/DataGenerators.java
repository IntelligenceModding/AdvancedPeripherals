package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
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

        generator.addProvider(event.includeServer(), new TurtleUpgradesProvider(packOutput));
        generator.addProvider(event.includeServer(), new PocketUpgradesProvider(packOutput));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(packOutput, completablefuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(packOutput));
    }
}
