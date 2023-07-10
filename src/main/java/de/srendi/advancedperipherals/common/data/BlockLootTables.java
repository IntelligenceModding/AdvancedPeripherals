package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class BlockLootTables extends net.minecraft.data.loot.BlockLoot {

    @Override
    protected void addTables() {
        APRegistration.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(this::dropSelf);
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return APRegistration.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
