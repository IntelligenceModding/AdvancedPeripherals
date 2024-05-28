package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagsProvider extends TagsProvider<Item> {

    protected ItemTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, Registry.ITEM, AdvancedPeripherals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(APTags.Items.SMART_GLASSES).add(APItems.SMART_GLASSES.get()).add(APItems.SMART_GLASSES_NETHERITE.get());
    }
}
