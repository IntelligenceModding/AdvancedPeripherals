package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    protected BlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        Registration.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(this::dropSelf);
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registration.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
