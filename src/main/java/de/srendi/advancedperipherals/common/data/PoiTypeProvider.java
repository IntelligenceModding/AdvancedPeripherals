package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;

import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PoiTypeProvider extends PoiTypeTagsProvider {

    public PoiTypeProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, AdvancedPeripherals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagsProvider.TagAppender<PoiType> appender = tag(PoiTypeTags.ACQUIRABLE_JOB_SITE);
        APRegistration.POI_TYPES.getEntries().stream().map(RegistryObject::getKey).forEach(appender::add);
    }

    @NotNull
    @Override
    public String getName() {
        return "AP POI Type Tags";
    }
}
