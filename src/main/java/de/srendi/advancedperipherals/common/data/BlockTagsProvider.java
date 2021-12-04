package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.IHarvesterBlock;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class BlockTagsProvider extends TagsProvider<Block> {
    /*
     * Just for note, this provider is designed also for external usage
     */

    private final @NotNull DeferredRegister<Block> blockRegistry;

    public BlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper, @NotNull DeferredRegister<Block> blockRegistry) {
        super(generator, Registry.BLOCK, AdvancedPeripherals.MOD_ID, existingFileHelper);
        this.blockRegistry = blockRegistry;
    }

    @Override
    protected void addTags() {
        blockRegistry.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            if (!(block instanceof IHarvesterBlock harvesterBlock))
                throw new IllegalArgumentException("For any block you should define harvester logic!");
            tag(harvesterBlock.getHarvestTag()).add(block);
            tag(harvesterBlock.getToolTag()).add(block);
        });
    }

    @Override
    protected Path getPath(ResourceLocation p_126561_) {
        return this.generator.getOutputFolder().resolve("data/" + p_126561_.getNamespace() + "/tags/blocks/" + p_126561_.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Block tags";
    }
}
