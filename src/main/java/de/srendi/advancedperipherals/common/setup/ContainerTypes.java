package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.registries.RegistryObject;

public class ContainerTypes {

    public static final DeferredHolder<MenuType<?>, MenuType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = Registration.CONTAINER_TYPES.register("memory_card_container", () -> IMenUType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.getCommandSenderWorld();
        return new InventoryManagerContainer(windowId, inv, pos, level);
    }));

    public static void register() {
    }
}
