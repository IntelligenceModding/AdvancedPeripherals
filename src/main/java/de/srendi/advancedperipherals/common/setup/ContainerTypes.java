package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

public class ContainerTypes {

    public static final RegistryObject<MenuType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = Registration.CONTAINER_TYPES.register("memory_card_container", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.getCommandSenderWorld();
        return new InventoryManagerContainer(windowId, inv, pos, level);
    }));

    public static void register() {
    }
}
