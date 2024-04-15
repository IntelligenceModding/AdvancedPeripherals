package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.common.data.ExistingFileHelper;
import net.neoforged.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PoiTypeProvider extends PoiTypeTagsProvider {


    public PoiTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, future, AdvancedPeripherals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagsProvider.TagAppender<PoiType> appender = tag(PoiTypeTags.ACQUIRABLE_JOB_SITE);
        Registration.POI_TYPES.getEntries().stream().map(RegistryObject::getKey).filter(Objects::nonNull).forEach(appender::add);
    }

    @NotNull
    @Override
    public String getName() {
        return "AP POI Type Tags";
    }
}
