package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.ImmutableSet;
import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.RegistryObject;

public class APVillagers {

    public static final RegistryObject<PoiType> COMPUTER_SCIENTIST_POI = APRegistration.POI_TYPES.register("computer_scientist", () -> new PoiType(ImmutableSet.copyOf(Registry.ModBlocks.COMPUTER_ADVANCED.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> COMPUTER_SCIENTIST = APRegistration.VILLAGER_PROFESSIONS.register("computer_scientist", () -> new VillagerProfession(AdvancedPeripherals.MOD_ID + ":computer_scientist", holder -> holder.is(COMPUTER_SCIENTIST_POI.getKey()), holder -> holder.is(COMPUTER_SCIENTIST_POI.getKey()), ImmutableSet.of(), ImmutableSet.of(Registry.ModBlocks.COMPUTER_ADVANCED.get()), SoundEvents.VILLAGER_WORK_TOOLSMITH));

    protected static void register() {
    }

}
