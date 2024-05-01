package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.shared.computer.core.ServerComputer;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SmartGlassesMenuProvider implements MenuProvider {
    private final ServerComputer computer;
    private final Component name;
    private final IItemHandler glassesContainer;

    public SmartGlassesMenuProvider(ServerComputer computer, ItemStack stack, IItemHandler glassesContainer) {
        this.computer = computer;
        name = stack.getHoverName();
        this.glassesContainer = glassesContainer;
    }

    @NotNull
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
                computer, inventory, glassesContainer, null
        );
    }
}
