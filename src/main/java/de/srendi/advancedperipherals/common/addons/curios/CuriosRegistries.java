package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.setup.APTags;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosTags;

import java.util.function.Function;

public final class CuriosRegistries {
    private CuriosRegistries() {}

    public static void registerTags(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagger) {
        tagger.apply(CuriosTags.HEAD)
            .addTag(APTags.Items.SMART_GLASSES);
    }
}
