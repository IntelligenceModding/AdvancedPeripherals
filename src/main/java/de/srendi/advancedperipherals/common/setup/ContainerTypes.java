package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ContainerTypes {

    public static final RegistryObject<ContainerType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = Registration.CONTAINER_TYPES
            .register("memory_card_container", () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new InventoryManagerContainer(windowId, inv, pos, world);
            }));

    public static void register() {

    }

}
