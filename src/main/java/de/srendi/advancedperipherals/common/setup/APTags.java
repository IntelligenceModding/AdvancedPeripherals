package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class APTags {

    public static void register() {
        Items.register();
    }

    public static class Items {

        // public static final TagKey<Item> SMART_GLASSES = tag("smart_glasses"); // unused

        public static void register() {
        }

        private static TagKey<Item> tag(@NotNull String name) {
            return TagKey.create(Registries.ITEM, AdvancedPeripherals.getRL(name));
        }
    }
}
