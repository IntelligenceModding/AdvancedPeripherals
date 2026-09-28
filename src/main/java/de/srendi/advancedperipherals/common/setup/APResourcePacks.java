package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class APResourcePacks {

    // For community made textures for the AP AE2 Disks
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {

            ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(AdvancedPeripherals.MOD_ID, "resourcepacks/funky_disks");

            event.addPackFinders(packLocation,
                    PackType.CLIENT_RESOURCES,
                    Component.literal("Funky Disks"),
                    PackSource.BUILT_IN, false,
                    Pack.Position.TOP);
        }
    }
}
