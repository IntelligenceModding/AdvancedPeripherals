package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ItemPropertiesRegistry {

    public static void register() {
        ItemProperties.register(APItems.MEMORY_CARD.get(), new ResourceLocation(AdvancedPeripherals.MOD_ID, "bounded"), (stack, level, entity, seed) -> {
            boolean bounded = stack.getOrCreateTag().contains("owner");
            return bounded ? 1 : 0;
        });
    }

}
