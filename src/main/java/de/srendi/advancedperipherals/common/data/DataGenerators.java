package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        generator.addProvider(event.includeServer(), new BlockTagsProvider(generator.getPackOutput(), completablefuture, existingFileHelper, Registration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new TurtleUpgradesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new PocketUpgradesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(generator.getPackOutput(), completablefuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(generator.getPackOutput(), existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(generator.getPackOutput()));
    }

}
