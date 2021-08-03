package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.ImmutableSet;
import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.fmllegacy.RegistryObject;

public class Villagers {

    public static final RegistryObject<PoiType> COMPUTER_SCIENTIST_POI = Registration.POI_TYPES.register("computer_scientist",
            () -> new PoiType("computer_scientist", ImmutableSet.copyOf(Registry.ModBlocks.COMPUTER_ADVANCED.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> COMPUTER_SCIENTIST = Registration.VILLAGER_PROFESSIONS.register("computer_scientist",
            () -> new VillagerProfession(AdvancedPeripherals.MOD_ID + ":" + "computer_scientist", COMPUTER_SCIENTIST_POI.get(),
                    ImmutableSet.of(Items.COMPUTER_TOOL.get()), ImmutableSet.of(Blocks.ENVIRONMENT_DETECTOR.get(), Blocks.PLAYER_DETECTOR.get(), Registry.ModBlocks.COMPUTER_ADVANCED.get()), SoundEvents.VILLAGER_WORK_ARMORER));

    public static void register() {
    }

}
