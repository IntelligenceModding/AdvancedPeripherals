package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.data.DataGenerator;
import net.neoforged.common.data.ExistingFileHelper;
import net.neoforged.data.event.GatherDataEvent;
import net.neoforged.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new BlockTagsProvider(generator, existingFileHelper, APRegistration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(generator));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(generator));
        generator.addProvider(event.includeServer(), new TurtleUpgradesProvider(generator));
        generator.addProvider(event.includeServer(), new PocketUpgradesProvider(generator));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(generator));
    }
}
