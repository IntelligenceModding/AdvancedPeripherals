package de.srendi.advancedperipherals.common.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class BlockLootTablesProvider extends LootTableProvider {

    public BlockLootTablesProvider(PackOutput output) {
        super(output, Set.of(), ImmutableList.of(new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)));
    }

    @Override
    @NotNull
    public List<SubProviderEntry> getTables() {
        return ImmutableList.of(new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK));
    }
}
