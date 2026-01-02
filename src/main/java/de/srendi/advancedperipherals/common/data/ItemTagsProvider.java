package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.setup.APTags;
import net.minecraft.core.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagsProvider extends TagsProvider<Item> {

    protected ItemTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, APRegistries.ITEM, AdvancedPeripherals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(APTags.Items.SMART_GLASSES)
            .add(APItems.SMART_GLASSES.getKey())
            .add(APItems.SMART_GLASSES_NETHERITE.getKey());
        AE2Registries.registerTags(this::tag);
    }
}
