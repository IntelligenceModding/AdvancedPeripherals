package de.srendi.advancedperipherals.common.village;

import com.mojang.datafixers.util.Pair;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import org.jline.utils.Log;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class VillageStructures {
    //Adapted from Pneumaticcraft
    public static void init() {
        //Ensure the vanilla static init is done
        PlainsVillagePools.init();
        SavannaVillagePools.init();
        TaigaVillagePools.init();
        DesertVillagePools.init();
        SnowyVillagePools.init();
        //Add the scientist house to each village biome
        for (String biome : new String[]{"desert", "snowy", "plains", "savanna", "taiga"}) {
            AdvancedPeripherals.debug("Register generating scientist_" + biome + " village house");
            addToPool(new ResourceLocation("village/" + biome + "/houses"),
                    AdvancedPeripherals.MOD_ID + ":villages/scientist_" + biome, 10);
        }
    }

    private static void addToPool(ResourceLocation pool, String toAdd, int weight) {
        JigsawPattern old = WorldGenRegistries.JIGSAW_POOL.getOrDefault(pool);
        if (old == null) {
            AdvancedPeripherals.debug("no jigsaw pool for " + pool + "? skipping villager house generation for it");
            return;
        }
        List<JigsawPiece> shuffled = old.getShuffledPieces(ThreadLocalRandom.current());
        List<Pair<JigsawPiece, Integer>> newPieces = shuffled.stream().map(p -> Pair.of(p, 1)).collect(Collectors.toList());
        JigsawPiece newPiece = JigsawPiece.func_242849_a(toAdd).apply(PlacementBehaviour.RIGID);
        newPieces.add(Pair.of(newPiece, weight));
        Registry.register(WorldGenRegistries.JIGSAW_POOL, pool, new JigsawPattern(pool, old.getName(), newPieces));
        AdvancedPeripherals.debug("Finished registration for " + toAdd);
    }
}
