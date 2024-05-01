package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IModuleItem {

    IModule createModule(SmartGlassesAccess access);

    /**
     * This method is called every tick the item is in the inventory of the smart glasses
     * Runs on the client and server side
     *
     * @param access The access to the smart glasses - Null on the client side
     * @param module The module - Null on the client side
     */
    default void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean isCurrentItem, @Nullable SmartGlassesAccess access, @Nullable IModule module) {

    }

}
