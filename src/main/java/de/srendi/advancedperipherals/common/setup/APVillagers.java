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
package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.ImmutableSet;
import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.RegistryObject;

public class APVillagers {

    public static final RegistryObject<PoiType> COMPUTER_SCIENTIST_POI = APRegistration.POI_TYPES
            .register("computer_scientist",
                    () -> new PoiType(ImmutableSet.copyOf(
                            Registry.ModBlocks.COMPUTER_ADVANCED.get().getStateDefinition().getPossibleStates()), 1,
                            1));

    public static final RegistryObject<VillagerProfession> COMPUTER_SCIENTIST = APRegistration.VILLAGER_PROFESSIONS
            .register("computer_scientist",
                    () -> new VillagerProfession(AdvancedPeripherals.MOD_ID + ":computer_scientist",
                            holder -> holder.is(COMPUTER_SCIENTIST_POI.getKey()),
                            holder -> holder.is(COMPUTER_SCIENTIST_POI.getKey()), ImmutableSet.of(),
                            ImmutableSet.of(Registry.ModBlocks.COMPUTER_ADVANCED.get()),
                            SoundEvents.VILLAGER_WORK_TOOLSMITH));

    protected static void register() {
    }

}
