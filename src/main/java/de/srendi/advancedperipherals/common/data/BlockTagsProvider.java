package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.IHarvesterBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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

    protected BlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper, DeferredRegister<Block> registry) {
        super(packOutput, ForgeRegistries.BLOCKS.getRegistryKey(), future, AdvancedPeripherals.MOD_ID, existingFileHelper);
        this.packOutput = packOutput;
        this.blockRegistry = registry;
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        blockRegistry.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            if (!(block instanceof IHarvesterBlock harvesterBlock))
                throw new IllegalArgumentException("For any block you should define harvester logic!");
            tag(harvesterBlock.getHarvestTag()).add(ForgeRegistries.BLOCKS.getResourceKey(block).get());
            tag(harvesterBlock.getToolTag()).add(ForgeRegistries.BLOCKS.getResourceKey(block).get());
        });
    }

    @Override
    protected Path getPath(ResourceLocation block) {
        return this.packOutput.getOutputFolder().resolve("data/" + block.getNamespace() + "/tags/blocks/" + block.getPath() + ".json");
    }

    @NotNull
    @Override
    public String getName() {
        return "Block tags";
    }
}
