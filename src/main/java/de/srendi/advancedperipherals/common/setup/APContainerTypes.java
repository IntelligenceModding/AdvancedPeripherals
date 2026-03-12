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
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public class APContainerTypes {

    public static final DeferredHolder<MenuType<?>, MenuType<InventoryManagerContainer>> INVENTORY_MANAGER_CONTAINER = APRegistration.CONTAINER_TYPES.register(
        "memory_card_container",
        () -> IMenuTypeExtension.create((windowId, inv, buf) -> {
            BlockPos pos = buf.readBlockPos();
            Level level = inv.player.getCommandSenderWorld();
            return new InventoryManagerContainer(windowId, inv, pos, level);
        })
    );

    public static final DeferredHolder<MenuType<KeyboardContainer>> KEYBOARD_CONTAINER = APRegistration.CONTAINER_TYPES.register(
        "keyboard_container",
        () -> IMenuTypeExtension.create((windowId, inv, buf) -> {
            ItemStack keyboardItem = ItemStack.STREAM_CODEC.decode(buf);
            Level level = inv.player.getCommandSenderWorld();
            return new KeyboardContainer(windowId, inv, level, keyboardItem);
        })
    );

    public static final DeferredHolder<MenuType<SmartGlassesContainer>> SMART_GLASSES_CONTAINER = APRegistration.CONTAINER_TYPES.register(
        "smart_glasses_container",
        () -> ContainerData.toType(
            ComputerContainerData.STREAM_CODEC,
            (id, inv, buf) -> new SmartGlassesContainer(id, player -> true, null, buf, inv, buf.displayStack())
        )
    );

    protected static void register() {
    }

}
