/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.village;

import com.mojang.datafixers.util.Pair;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.DesertVillagePools;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.SavannaVillagePools;
import net.minecraft.data.worldgen.SnowyVillagePools;
import net.minecraft.data.worldgen.TaigaVillagePools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.stream.Collectors;

public class VillageStructures {

    // Adapted from Pneumaticcraft
    public static void init() {
        if (!APConfig.WORLD_CONFIG.enableVillagerStructures.get())
            return;
        // Ensure the vanilla static init is done
        PlainVillagePools.bootstrap();
        SavannaVillagePools.bootstrap();
        TaigaVillagePools.bootstrap();
        DesertVillagePools.bootstrap();
        SnowyVillagePools.bootstrap();
        // Add the scientist house to each village biome
        for (String biome : new String[]{"desert", "snowy", "plains", "savanna", "taiga"}) {
            AdvancedPeripherals.debug("Register generating scientist_" + biome + " village house");
            addToPool(new ResourceLocation("village/" + biome + "/houses"),
                    AdvancedPeripherals.MOD_ID + ":villages/scientist_" + biome,
                    APConfig.WORLD_CONFIG.villagerStructureWeight.get());
        }
    }

    private static void addToPool(ResourceLocation pool, String toAdd, int weight) {
        StructureTemplatePool old = BuiltinRegistries.TEMPLATE_POOL.get(pool);
        if (old == null) {
            AdvancedPeripherals.debug("no jigsaw pool for " + pool + "? skipping villager house generation for it");
            return;
        }
        List<StructurePoolElement> shuffled = old.getShuffledTemplates(RandomSource.createNewThreadLocalInstance());
        List<Pair<StructurePoolElement, Integer>> newPieces = shuffled.stream().map(p -> Pair.of(p, 1))
                .collect(Collectors.toList());
        StructurePoolElement newPiece = StructurePoolElement.legacy(toAdd)
                .apply(StructureTemplatePool.Projection.RIGID);
        newPieces.add(Pair.of(newPiece, weight));
        Registry.register(BuiltinRegistries.TEMPLATE_POOL, pool,
                new StructureTemplatePool(pool, old.getName(), newPieces));
        AdvancedPeripherals.debug("Finished registration for " + toAdd);
    }
}
