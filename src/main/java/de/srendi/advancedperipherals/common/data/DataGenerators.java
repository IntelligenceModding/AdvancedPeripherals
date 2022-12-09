package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new BlockTagsProvider(generator.getPackOutput(), null, existingFileHelper, Registration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new TurtleUpgradesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new PocketUpgradesProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new PoiTypeProvider(generator.getPackOutput(), null, existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(generator));
    }

}
