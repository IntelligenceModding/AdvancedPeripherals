package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    protected BlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        Registration.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(registeredBlock -> {
            //Allow blocks to transfer their name to the dropped block when broken
            this.add(registeredBlock, (block) -> this.createNameableBlockEntityTable(block));
        });
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registration.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
