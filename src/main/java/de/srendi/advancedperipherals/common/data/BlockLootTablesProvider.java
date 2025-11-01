package de.srendi.advancedperipherals.common.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BlockLootTablesProvider extends LootTableProvider {

    public BlockLootTablesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), ImmutableList.of(new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)), lookupProvider);
    }

    @Override
    public @NotNull List<SubProviderEntry> getTables() {
        return ImmutableList.of(new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(@NotNull WritableRegistry<LootTable> writableregistry, @NotNull ValidationContext validationcontext, ProblemReporter.@NotNull Collector collector) {
        super.validate(writableregistry, validationcontext, collector);
    }
}
