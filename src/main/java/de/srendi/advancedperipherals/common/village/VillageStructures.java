package de.srendi.advancedperipherals.common.village;

import com.mojang.datafixers.util.Pair;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class VillageStructures {
    //Adapted from Pneumaticcraft
    public static void init() {
        if (!APConfig.WORLD_CONFIG.ENABLE_VILLAGER_STRUCTURES.get())
            return;
        //Ensure the vanilla static init is done
        PlainsVillagePools.bootstrap();
        SavannaVillagePools.bootstrap();
        TaigaVillagePools.bootstrap();
        DesertVillagePools.bootstrap();
        SnowyVillagePools.bootstrap();
        //Add the scientist house to each village biome
        for (String biome : new String[]{"desert", "snowy", "plains", "savanna", "taiga"}) {
            AdvancedPeripherals.debug("Register generating scientist_" + biome + " village house");
            addToPool(new ResourceLocation("village/" + biome + "/houses"),
                    AdvancedPeripherals.MOD_ID + ":villages/scientist_" + biome, APConfig.WORLD_CONFIG.VILLAGER_STRUCTURE_WEIGHT.get());
        }
    }

    private static void addToPool(ResourceLocation pool, String toAdd, int weight) {
        JigsawPattern old = WorldGenRegistries.TEMPLATE_POOL.get(pool);
        if (old == null) {
            AdvancedPeripherals.debug("no jigsaw pool for " + pool + "? skipping villager house generation for it");
            return;
        }
        List<JigsawPiece> shuffled = old.getShuffledTemplates(ThreadLocalRandom.current());
        List<Pair<JigsawPiece, Integer>> newPieces = shuffled.stream().map(p -> Pair.of(p, 1)).collect(Collectors.toList());
        JigsawPiece newPiece = JigsawPiece.legacy(toAdd).apply(PlacementBehaviour.RIGID);
        newPieces.add(Pair.of(newPiece, weight));
        Registry.register(WorldGenRegistries.TEMPLATE_POOL, pool, new JigsawPattern(pool, old.getName(), newPieces));
        AdvancedPeripherals.debug("Finished registration for " + toAdd);
    }
}
