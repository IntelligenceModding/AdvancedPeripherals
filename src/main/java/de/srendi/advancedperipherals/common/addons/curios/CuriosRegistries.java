package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.setup.APTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class CuriosRegistries {
    private CuriosRegistries() {}

    public static void registerTags(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagger) {
        tagger.apply(TagKey.create(Registries.ITEM, new ResourceLocation("curios", "head")))
            .addTag(APTags.Items.SMART_GLASSES);
    }
}
