package de.srendi.advancedperipherals.common.smartglasses;

import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmartGlassesMenuProvider implements MenuProvider {
    private final SmartGlassesComputer computer;
    private final ItemStack stack;
    private final IItemHandler glassesContainer;

    public SmartGlassesMenuProvider(SmartGlassesComputer computer, ItemStack stack, IItemHandler glassesContainer) {
        this.computer = computer;
        this.stack = stack;
        this.glassesContainer = glassesContainer;
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return this.stack.getHoverName();
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player entity) {
        return new SmartGlassesContainer(
            id,
            player -> SmartGlassesItem.containsGlassesStack(player, stack -> this.stack == stack),
            computer,
            inventory,
            glassesContainer
        );
    }
}
