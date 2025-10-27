package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.registries.RegistryObject;

public class APContainerTypes {

    public static final DeferredHolder<MenuType<?>, MenuType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = APRegistration.CONTAINER_TYPES.register("memory_card_container", () -> IMenuTypeExtension.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.getCommandSenderWorld();
        return new InventoryManagerContainer(windowId, inv, pos, level);
    }));

    public static final RegistryObject<MenuType<SmartGlassesContainer>> SMART_GLASSES_CONTAINER = APRegistration.CONTAINER_TYPES.register("smart_glasses_container", () -> ContainerData.toType(ComputerContainerData::new,
            (id, inv, data) -> new SmartGlassesContainer(id, player -> true, null, data, inv, data.displayStack())
    ));

    protected static void register() {
    }

}
