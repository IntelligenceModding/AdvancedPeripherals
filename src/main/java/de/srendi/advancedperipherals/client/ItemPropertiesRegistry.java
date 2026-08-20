package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.setup.APItems;
import net.minecraft.client.renderer.item.ItemProperties;

public class ItemPropertiesRegistry {

    public static void register() {
        ItemProperties.register(APItems.MEMORY_CARD.get(), AdvancedPeripherals.getRL("bounded"), (stack, level, entity, seed) -> {
            return stack.hasTag() && stack.getTag().contains(APDataComponents.OWNER) ? 1 : 0;
        });
    }

}
