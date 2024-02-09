package de.srendi.advancedperipherals.common.village;

import com.mojang.datafixers.util.Pair;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillageStructures {

    // Adapted from Pneumaticcraft
    private static void addPieceToPool(Registry<StructureTemplatePool> templatePoolRegistry, Holder<StructureProcessorList> emptyProcessor, ResourceLocation poolRL, String nbtPieceRL, StructureTemplatePool.Projection projection, int weight) {
        // Grab the pool we want to add to
        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null)
            return;

        // Grabs the nbt piece and creates a SingleJigsawPiece of it that we can add to a structure's pool.
        // Note: street pieces are a legacy_single_pool_piece type, houses are single_pool_piece
        SinglePoolElement piece = poolRL.getPath().endsWith("streets") ?
                SinglePoolElement.legacy(nbtPieceRL, emptyProcessor).apply(projection) :
                SinglePoolElement.single(nbtPieceRL, emptyProcessor).apply(projection);

        // Weight is handled by how many times the entry appears in this list.
        // We do not need to worry about immutability as this field is created using Lists.newArrayList(); which makes a mutable list.
        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        // This list of pairs of pieces and weights is not used by vanilla by default but another mod may need it for efficiency.
        // So let's add to this list for completeness. We need to make a copy of the array as it can be an immutable list.
        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }

    @SubscribeEvent
    public static void addStructures(ServerAboutToStartEvent event) {
        if (!APConfig.WORLD_CONFIG.enableVillagerStructures.get())
            return;

        Holder<StructureProcessorList> emptyProcessor = event.getServer().registryAccess().registryOrThrow(Registries.PROCESSOR_LIST)
                .getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("minecraft:empty")));

        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);

        for (String biome : new String[]{"desert", "snowy", "plains", "savanna", "taiga"}) {
            AdvancedPeripherals.debug("Register generating scientist_" + biome + " village house");
            addPieceToPool(templatePoolRegistry, emptyProcessor, new ResourceLocation("village/" + biome + "/houses"), AdvancedPeripherals.MOD_ID + ":villages/scientist_" + biome, StructureTemplatePool.Projection.RIGID, APConfig.WORLD_CONFIG.villagerStructureWeight.get());
        }
    }
}
