package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

public class APContainerTypes {

    public static final RegistryObject<MenuType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = APRegistration.CONTAINER_TYPES.register("memory_card_container", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.getCommandSenderWorld();
        return new InventoryManagerContainer(windowId, inv, pos, level);
    }));

    public static final RegistryObject<MenuType<KeyboardContainer>> KEYBOARD_CONTAINER = APRegistration.CONTAINER_TYPES.register("keyboard_container", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        ItemStack keyboardItem = data.readItem();
        Level level = inv.player.getCommandSenderWorld();
        return new KeyboardContainer(windowId, inv, pos, level, keyboardItem);
    }));

    public static final RegistryObject<MenuType<SmartGlassesContainer>> SMART_GLASSES_CONTAINER = APRegistration.CONTAINER_TYPES.register("smart_glasses_container", () -> ContainerData.toType(ComputerContainerData::new,
            (id, inv, data) -> new SmartGlassesContainer(id, player -> true, null, data, inv, data.displayStack())
    ));

    protected static void register() {
    }

}
