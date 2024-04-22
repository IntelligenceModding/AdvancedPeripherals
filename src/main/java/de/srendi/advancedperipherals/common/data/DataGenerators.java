package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.common.data.ExistingFileHelper;
import net.neoforged.data.event.GatherDataEvent;
import net.neoforged.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        generator.addProvider(event.includeServer(), new BlockTagsProvider(packOutput, completablefuture, existingFileHelper, Registration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(packOutput));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(packOutput));
        generator.addProvider(event.includeServer(), new TurtleUpgradesProvider(packOutput));
        generator.addProvider(event.includeServer(), new PocketUpgradesProvider(packOutput));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(packOutput, completablefuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(packOutput));
    }

}
