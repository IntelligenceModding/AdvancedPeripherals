package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.Nullable;

public class NamedContainerProvider implements MenuProvider {

    protected Component component;
    protected MenuConstructor containerProvider;

    public NamedContainerProvider(Component component, MenuConstructor containerProvider) {
        this.component = component;
        this.containerProvider = containerProvider;
    }

    @Override
    public Component getDisplayName() {
        return component;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return containerProvider.createMenu(id, playerInventory, playerEntity);
    }
}
