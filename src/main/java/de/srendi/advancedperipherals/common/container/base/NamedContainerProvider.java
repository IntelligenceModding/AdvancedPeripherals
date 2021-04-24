package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;

public class NamedContainerProvider implements INamedContainerProvider {

    protected ITextComponent component;
    protected IContainerProvider containerProvider;

    public NamedContainerProvider(ITextComponent component, IContainerProvider containerProvider) {
        this.component = component;
        this.containerProvider = containerProvider;
    }

    @Override
    public ITextComponent getDisplayName() {
        return component;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return containerProvider.createMenu(id, playerInventory, playerEntity);
    }
}
