package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.container.MemoryCardContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ContainerTypes {

    public static final RegistryObject<ContainerType<MemoryCardContainer>> MEMORY_CARD = Registration.CONTAINER_TYPES
            .register("memory_card_container", () -> IForgeContainerType.create(MemoryCardContainer::new));

    public static void register() {

    }

}
