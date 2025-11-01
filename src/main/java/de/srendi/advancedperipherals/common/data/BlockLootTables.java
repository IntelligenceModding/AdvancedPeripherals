package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    protected BlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        APRegistration.BLOCKS.getEntries().stream().map(DeferredHolder::get).forEach(registeredBlock -> {
            //Allow blocks to transfer their name to the dropped block when broken
            this.add(registeredBlock, this::createNameableBlockEntityTable);
        });
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return APRegistration.BLOCKS.getEntries().stream().map(DeferredHolder<Block>::get)::iterator;
    }
}
