package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.IHarvestableBlock;
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

    @NotNull
    private final DeferredRegister<Block> blockRegistry;
    @NotNull
    private final DataGenerator generator;

    public BlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper, @NotNull DeferredRegister<Block> blockRegistry) {
        super(generator, Registry.BLOCK, AdvancedPeripherals.MOD_ID, existingFileHelper);
        this.blockRegistry = blockRegistry;
        this.generator = generator;
    }

    @Override
    protected void addTags() {
        blockRegistry.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            if (!(block instanceof IHarvestableBlock harvesterBlock))
                throw new IllegalArgumentException("For any block you should define harvester logic!");
            tag(harvesterBlock.getHarvestTag()).add(block);
            tag(harvesterBlock.getToolTag()).add(block);
        });
    }

    @Override
    protected Path getPath(ResourceLocation block) {
        return this.generator.getOutputFolder().resolve("data/" + block.getNamespace() + "/tags/blocks/" + block.getPath() + ".json");
    }

    @NotNull
    @Override
    public String getName() {
        return "Block tags";
    }
}
