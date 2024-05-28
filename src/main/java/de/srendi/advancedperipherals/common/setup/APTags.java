package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class APTags {

    public static class Items {

        public static final TagKey<Item> SMART_GLASSES = tag("smart_glasses");

        public static void init() {

        }

        private static TagKey<Item> tag(@NotNull String name) {
            return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(AdvancedPeripherals.MOD_ID, name));
        }
    }
}
