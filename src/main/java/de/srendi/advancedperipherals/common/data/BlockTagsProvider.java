package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.IHarvestableBlock;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends TagsProvider<Block> {
    /*
     * Just for note, this provider is designed also for external usage
     */

    @NotNull
    private final DeferredRegister<Block> blockRegistry;
    @NotNull
    private final PackOutput packOutput;

    protected BlockTagsProvider(@NotNull PackOutput packOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper, DeferredRegister<Block> registry) {
        super(packOutput, Registries.BLOCK, future, AdvancedPeripherals.MOD_ID, existingFileHelper);
        this.packOutput = packOutput;
        this.blockRegistry = registry;
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        for (RegistryObject<Block> holder : blockRegistry.getEntries()) {
            if (!(holder.get() instanceof IHarvestableBlock harvesterBlock)) {
                throw new IllegalArgumentException("For any block you should define harvester logic!");
            }
            tag(harvesterBlock.getHarvestTag()).add(holder.getKey());
            tag(harvesterBlock.getToolTag()).add(holder.getKey());
        }

        tag(BlockTags.RAILS).add((ResourceKey<Block>) (ResourceKey<? extends Block>) APBlocks.SMART_RAIL.getKey());
    }

    @Override
    protected Path getPath(ResourceLocation block) {
        return this.packOutput.getOutputFolder().resolve("data/" + block.getNamespace() + "/tags/blocks/" + block.getPath() + ".json");
    }

    @Override
    @NotNull
    public String getName() {
        return "Block tags";
    }
}
