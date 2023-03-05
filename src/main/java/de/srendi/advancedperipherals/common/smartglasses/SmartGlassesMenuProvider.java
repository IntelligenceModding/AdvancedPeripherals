package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SmartGlassesMenuProvider implements MenuProvider {
    private final ServerComputer computer;
    private final Component name;
    private final SmartGlassesItem item;
    private final InteractionHand hand;
    private final boolean isTypingOnly;

    public SmartGlassesMenuProvider(ServerComputer computer, ItemStack stack, SmartGlassesItem item, InteractionHand hand, boolean isTypingOnly) {
        this.computer = computer;
        name = stack.getHoverName();
        this.item = item;
        this.hand = hand;
        this.isTypingOnly = isTypingOnly;
    }


    @Override
    public Component getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player entity) {
        return new SmartGlassesContainer(id,
                p -> {
                return true;
            },
                computer
        );
    }
}
